package uk.ac.ebi.ep.sitemap.advanced;

import java.io.*;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.zip.GZIPOutputStream;
import org.apache.log4j.Logger;
import org.xml.sax.SAXException;

abstract class SitemapGenerator<U extends ISitemapUrl, THIS extends SitemapGenerator<U, THIS>> {

    /**
     * 50000 URLs per sitemap maximum
     */
    public static final int MAX_URLS_PER_SITEMAP = 50000;
    private final String baseUrl;
    private final File baseDir;
    private final String fileNamePrefix;
    private final String fileNameSuffix;
    private final boolean allowMultipleSitemaps;
    private final ArrayList<U> urls = new ArrayList<U>();
    private final W3CDateFormat dateFormat;
    private final int maxUrls;
    private final boolean autoValidate;
    private final boolean gzip;
    private final ISitemapUrlRenderer<U> renderer;
    private int mapCount = 0;
    private boolean finished = false;
    private final ArrayList<File> outFiles = new ArrayList<File>();
    private  File outFile;
    private final Logger LOGGER = Logger.getLogger(SitemapGenerator.class);

    public SitemapGenerator(AbstractSitemapGeneratorOptions<?> options, ISitemapUrlRenderer<U> renderer) {
        baseDir = options.baseDir;
        baseUrl = options.baseUrl;

        fileNamePrefix = options.fileNamePrefix;
        W3CDateFormat dateFormat = options.dateFormat;
        if (dateFormat == null) {
            dateFormat = new W3CDateFormat();
        }
        this.dateFormat = dateFormat;
        allowMultipleSitemaps = options.allowMultipleSitemaps;
        maxUrls = options.maxUrls;
        autoValidate = options.autoValidate;
        gzip = options.gzip;
        this.renderer = renderer;
        fileNameSuffix = gzip ? ".xml.gz" : ".xml";
         //fileNameSuffix = gzip ? ".txt.gz" : ".txt";//seems google prefers the data to be in .txt extention.
    }

    /**
     * Add one URL of the appropriate type to this sitemap. If we have reached
     * the maximum number of URLs, we'll throw an exception if {@link #allowMultipleSitemaps}
     * is false, or else write out one sitemap immediately.
     *
     * @param url the URL to add to this sitemap
     * @return this
     */
    public THIS addUrl(U url) {
        if (finished) {
            throw new RuntimeException("Sitemap already printed; you must create a new generator to make more sitemaps");
        }
       // UrlUtils.checkUrl(url.getUrl().toString(), baseUrl);
        if (urls.size() == maxUrls) {
            if (!allowMultipleSitemaps) {
                throw new RuntimeException("More than " + maxUrls + " urls, but allowMultipleSitemaps is false.  Enable allowMultipleSitemaps to split the sitemap into multiple files with a sitemap index.");
            }
            if (mapCount == 0) {
                mapCount++;
            }
            writeSiteMap();
            mapCount++;
            urls.clear();
        }
        urls.add(url);
        return getThis();
    }

    /**
     * Add multiple URLs of the appropriate type to this sitemap, one at a time.
     * If we have reached the maximum number of URLs, we'll throw an exception
     * if {@link #allowMultipleSitemaps} is false, or write out one sitemap
     * immediately.
     *
     * @param urls the URLs to add to this sitemap
     * @return this
     */
    public THIS addUrls(Iterable<? extends U> urls) {
        for (U url : urls) {
            addUrl(url);
        }
        return getThis();
    }

    /**
     * Add multiple URLs of the appropriate type to this sitemap, one at a time.
     * If we have reached the maximum number of URLs, we'll throw an exception
     * if {@link #allowMultipleSitemaps} is false, or write out one sitemap
     * immediately.
     *
     * @param urls the URLs to add to this sitemap
     * @return this
     */
    public THIS addUrls(U... urls) {
        for (U url : urls) {
            addUrl(url);
        }
        return getThis();
    }

    /**
     * Add multiple URLs of the appropriate type to this sitemap, one at a time.
     * If we have reached the maximum number of URLs, we'll throw an exception
     * if {@link #allowMultipleSitemaps} is false, or write out one sitemap
     * immediately.
     *
     * @param urls the URLs to add to this sitemap
     * @return this
     * @throws MalformedURLException
     */
    public THIS addUrls(String... urls) throws MalformedURLException {
        for (String url : urls) {
            addUrl(url);
        }
        return getThis();
    }

    /**
     * Add one URL of the appropriate type to this sitemap. If we have reached
     * the maximum number of URLs, we'll throw an exception if {@link #allowMultipleSitemaps}
     * is false, or else write out one sitemap immediately.
     *
     * @param url the URL to add to this sitemap
     * @return this
     * @throws MalformedURLException
     */
    public THIS addUrl(String url) throws MalformedURLException {
        U sitemapUrl;
        try {
            sitemapUrl = renderer.getUrlClass().getConstructor(String.class).newInstance(url);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
        return addUrl(sitemapUrl);
    }

    /**
     * Add multiple URLs of the appropriate type to this sitemap, one at a time.
     * If we have reached the maximum number of URLs, we'll throw an exception
     * if {@link #allowMultipleSitemaps} is false, or write out one sitemap
     * immediately.
     *
     * @param urls the URLs to add to this sitemap
     * @return this
     */
    public THIS addUrls(URL... urls) {
        for (URL url : urls) {
            addUrl(url);
        }
        return getThis();
    }

    /**
     * Add one URL of the appropriate type to this sitemap. If we have reached
     * the maximum number of URLs, we'll throw an exception if {@link #allowMultipleSitemaps}
     * is false, or write out one sitemap immediately.
     *
     * @param url the URL to add to this sitemap
     * @return this
     */
    public THIS addUrl(URL url) {
        U sitemapUrl;
        try {
            sitemapUrl = renderer.getUrlClass().getConstructor(URL.class).newInstance(url);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
        return addUrl(sitemapUrl);
    }

    @SuppressWarnings("unchecked")
    THIS getThis() {
        return (THIS) this;
    }
   
    // This method is to be used only for JUnit test purpose. it is an easy method to delete generated file from the local machine.

    public void deleteGeneratedSiteMap() {
        if (!outFiles.isEmpty()) {
            for (File file : outFiles) {
                file.delete();

            }
        }
        if (outFile.exists()) {
            outFile.delete();
        }
//        if(zippedFile.exists()){
//            
//            zippedFile.delete();
//        }

    }
    

    
    
    //public File zippedFile = null;
    //to zip the sitemap index
        protected void makeGzip(File file) throws IOException {
        BufferedInputStream bin = null;
        GZIPOutputStream gzos = null;
           
        //File file = new File(theFile.getName() + ".gz");
        //zippedFile = new File(file.getName() + ".gz");

        try {

            FileInputStream fin = new FileInputStream(file);
            bin = new BufferedInputStream(fin);
            FileOutputStream fos = new FileOutputStream(file + ".gz");
            //FileOutputStream fos = new FileOutputStream(file);
            gzos = new GZIPOutputStream(fos);
            byte[] buf = new byte[1024];
            int len;
            while ((len = bin.read(buf)) > -1) {
                gzos.write(buf, 0, len);
            }
        } catch (IOException e) {
            LOGGER.fatal("Error while gzipping!", e);
        } finally {
            if (bin != null) {
                bin.close();
            }
            if (gzos != null) {

                gzos.close();
            }
        }
        //return file;

    }

    /**
     * Write out remaining URLs; this method can only be called once. This is
     * necessary so we can keep an accurate count for {@link #writeSitemapsWithIndex()}.
     *
     * @return a list of files we wrote out to disk
     */
    public List<File> write() {
        if (finished) {
            throw new RuntimeException("Sitemap already printed; you must create a new generator to make more sitemaps");
        }
        if (urls.isEmpty() && mapCount == 0) {
            throw new RuntimeException("No URLs added, sitemap would be empty; you must add some URLs with addUrls");
        }
        writeSiteMap();
        finished = true;
        return outFiles;
    }

    /**
     * After you've called {@link #write()}, call this to generate a sitemap
     * index of all sitemaps you generated.
     *
     */
    public void writeSitemapsWithIndex(String sitemapIndex) {
        //modify this bit if you want to customize -- @Joseph
        if (!finished) {
            throw new RuntimeException("Sitemaps not generated yet; call write() first");
        }

        outFile = new File(baseDir, sitemapIndex);
        //File outFile = new File(baseDir, sitemapIndex);
        //File outFile = new File(baseDir, "sitemap_index.xml");
        SitemapIndexGenerator sig;

        try {
            sig = new SitemapIndexGenerator.Options(baseUrl, outFile).dateFormat(dateFormat).autoValidate(autoValidate).build();

        } catch (MalformedURLException e) {
            throw new RuntimeException("bug", e);
        }
        sig.addUrls(fileNamePrefix, fileNameSuffix, mapCount).write();
//        try {
//                     makeGzip(outFile);
//           
//           
//        } catch (IOException ex) {
//            LOGGER.fatal("Output file cannot be zipped", ex);
//        }
    }

    private void writeSiteMap() {
        if (urls.size() == 0) {
            return;
        }
        String fileNamePrefix;
        if (mapCount > 0) {
            fileNamePrefix = this.fileNamePrefix + mapCount;
        } else {
            fileNamePrefix = this.fileNamePrefix;
        }
        File outFile = new File(baseDir, fileNamePrefix + fileNameSuffix);
        outFiles.add(outFile);
        try {
            OutputStreamWriter out;
            if (gzip) {
                FileOutputStream fileStream = new FileOutputStream(outFile);
                GZIPOutputStream gzipStream = new GZIPOutputStream(fileStream);
                out = new OutputStreamWriter(gzipStream);
            } else {
                out = new FileWriter(outFile);
            }

            writeSiteMap(out);
            if (autoValidate) {
                SitemapValidator.validateWebSitemap(outFile);
            }
        } catch (IOException e) {
            throw new RuntimeException("Problem writing sitemap file " + outFile, e);
        } catch (SAXException e) {
            throw new RuntimeException("Sitemap file failed to validate (bug?)", e);
        }
    }

    private void writeSiteMap(OutputStreamWriter out) throws IOException {
          out.write("<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n");
        out.write("<urlset xmlns=\"http://www.sitemaps.org/schemas/sitemap/0.9\">\n");
        //out.write("<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n");
        //out.write("<urlset xmlns=\"http://www.sitemaps.org/schemas/sitemap/0.9\"");


        //out.write("<urlset xmlns=\"http://www.sitemaps.org/schemas/sitemap/0.9\" " + " xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\"" + " xsi:schemaLocation=\"http://www.sitemaps.org/schemas/sitemap/0.9"
                //+ "http://www.sitemaps.org/schemas/sitemap/0.9/sitemap.xsd\"");

        if (renderer.getXmlNamespaces() != null) {
            out.write(renderer.getXmlNamespaces());
            out.write(' ');
        }
        //out.write(">\n");
        for (U url : urls) {
            renderer.render(url, out, dateFormat);
        }
        out.write("</urlset>");
        out.close();
    }
}

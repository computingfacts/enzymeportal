package uk.ac.ebi.ep.controller;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.ApplicationContext;
import org.springframework.web.context.support.WebApplicationContextUtils;
import uk.ac.ebi.ep.mBean.FilesConfig;

/**
 * Servlet acting as proxy to retrieve files from the file system, out of the
 * web container.
 * <br/>
 * It requires a context parameter <b><code>FileProxyServlet.fs.base</code></b>
 * set to the path to the directory in the file system used as base for the
 * file requests. This directory should be readable for the unix user running
 * the server, and from the machine where it runs.
 * @author rafa
 * @since 1.0.25
 */
public class FileProxyServlet extends HttpServlet {

   private final Logger LOGGER = LoggerFactory.getLogger(FileProxyServlet.class);

    @Override
    protected void service(HttpServletRequest req, HttpServletResponse resp)
    throws ServletException, IOException {
        ApplicationContext context = WebApplicationContextUtils
                .getWebApplicationContext(getServletContext());
        FilesConfig config = context.getBean(FilesConfig.class);
        File f = new File(config.getBaseDirectory(),
                req.getPathInfo().substring(1));
        if (f.exists() && f.canRead()){
            String extension = req.getPathInfo()
                    .substring(req.getPathInfo().lastIndexOf(".") + 1);
            String contentType = "text/plain;charset=UTF-8";
            if ("xml".equals(extension)){
                contentType = "text/xml;charset=UTF-8";
            }
            // Add here any other content types when (if) we serve them.
            resp.setContentType(contentType);
            FileInputStream fis = null;
            BufferedInputStream bis = null;
            try {
                fis = new FileInputStream(f);
                bis = new BufferedInputStream(fis);
                byte[] buffer = new byte[256];
                int r = -1;
                while ((r = bis.read(buffer)) != -1){
                    resp.getOutputStream().write(buffer, 0, r);
                }
                resp.flushBuffer();
            } finally {
                if (fis != null) fis.close();
                if (bis != null) bis.close();
            }
        } else {
            LOGGER.error("File not found: " + f);
            resp.sendError(HttpServletResponse.SC_NOT_FOUND);
        }
    }

}

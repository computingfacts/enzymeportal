/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package uk.ac.ebi.ep.controller;

import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;
import javax.json.Json;
import javax.json.JsonArray;
import javax.json.JsonObject;
import javax.json.JsonReader;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import uk.ac.ebi.ep.base.search.EnzymeFinder;
import static uk.ac.ebi.ep.controller.AbstractController.LOGGER;
import uk.ac.ebi.ep.data.common.CommonSpecies;
import uk.ac.ebi.ep.data.domain.UniprotEntry;
import uk.ac.ebi.ep.data.search.model.Compound;
import uk.ac.ebi.ep.data.search.model.Disease;
import uk.ac.ebi.ep.data.search.model.EcNumber;
import uk.ac.ebi.ep.data.search.model.SearchFilters;
import uk.ac.ebi.ep.data.search.model.SearchModel;
import uk.ac.ebi.ep.data.search.model.SearchParams;
import uk.ac.ebi.ep.data.search.model.SearchResults;
import uk.ac.ebi.ep.data.search.model.Species;
import uk.ac.ebi.ep.enzymes.EnzymeEntry;
import uk.ac.ebi.ep.enzymes.EnzymeSubSubclass;
import uk.ac.ebi.ep.enzymes.EnzymeSubclass;
import uk.ac.ebi.ep.enzymes.IntenzEnzyme;

/**
 *
 * @author joseph
 */
@Controller
public class BrowseEnzymesController extends AbstractController {

    //concrete jsp's
    private static final String BROWSE_ENZYMES = "/browse_enzymes";
    private static final String EC = "/ec";
  
    private static final String RESULT = "/searches";
    //abtract url
    private static final String BROWSE_ENZYME_CLASSIFICATION = "/browse/enzymes";
    private static final String BROWSE_EC = "/browse/enzyme";

    private static final String FIND_SPECIES_BY_EC = "/species-by-ec";


    private static final String SEARCH_ENZYMES = "/search-enzymes";
    private static final String EC_NUMBER = "ec";
    private static final String NAME = "name";
    private static final String DESCRIPTION = "description";
    private static final String SUBCLASSES = "subclasses";
    private static final String SUBSUBCLASSES = "subsubclasses";
    private static final String ENTRIES = "entries";
    private static final String INTENZ_URL = "http://www.ebi.ac.uk/intenz/ws/EC";
    private static final String ROOT = "ROOT";
    private static final String SUBCLASS = "SUBCLASS";
    private static final String SUBSUBCLASS = "SUBSUBCLASS";
    private static final String selectedEc = "selectedEc";


    private static final int SEARCH_PAGESIZE = 10;
    


    @RequestMapping(value = "/species/{ec}", method = RequestMethod.GET)
    public String getSpecies(@PathVariable("ec") String ec, Model model, RedirectAttributes attributes) {

        long startTime = System.nanoTime();
        Pageable pageable = new PageRequest(0, SEARCH_PAGESIZE);

        Page<UniprotEntry> page = this.enzymePortalService.findEnzymesByEcNumber(ec, pageable);

        long endTime = System.nanoTime();
        long duration = endTime - startTime;
        long elapsedtime = TimeUnit.SECONDS.convert(duration, TimeUnit.NANOSECONDS);
        LOGGER.warn("Duration :  (" + elapsedtime + " sec)");

        List<UniprotEntry> species = page.getContent();//.stream().map(EnzymePortal::new).distinct().map(EnzymePortal::unwrapProtein).filter(Objects::nonNull).collect(Collectors.toList());

        int current = page.getNumber() + 1;
        int begin = Math.max(1, current - 5);
        int end = Math.min(begin + 10, page.getTotalPages());

        model.addAttribute("page", page);
        model.addAttribute("beginIndex", begin);
        model.addAttribute("endIndex", end);
        model.addAttribute("currentIndex", current);

        model.addAttribute("species", species);
        model.addAttribute("ec", ec);

        return "species";
    }

  
    @RequestMapping(value = "/species/{ec}/page/{pageNumber}/", method = RequestMethod.GET)
    public String getSpeciesPaginated(@PathVariable("ec") String ec, @PathVariable("pageNumber") Integer pageNumber, Model model, RedirectAttributes attributes) {

        if (pageNumber < 1) {
            pageNumber = 1;
        }

        long startTime = System.nanoTime();
        Pageable pageable = new PageRequest(pageNumber - 1, SEARCH_PAGESIZE);

        Page<UniprotEntry> page = this.enzymePortalService.findEnzymesByEcNumber(ec, pageable);

        long endTime = System.nanoTime();
        long duration = endTime - startTime;
        long elapsedtime = TimeUnit.SECONDS.convert(duration, TimeUnit.NANOSECONDS);
        LOGGER.warn("Duration :  (" + elapsedtime + " sec)");

        List<UniprotEntry> species = page.getContent();//.stream().map(EnzymePortal::new).distinct().map(EnzymePortal::unwrapProtein).filter(Objects::nonNull).collect(Collectors.toList());

        int current = page.getNumber() + 1;
        int begin = Math.max(1, current - 5);
        int end = Math.min(begin + 10, page.getTotalPages());

        model.addAttribute("page", page);
        model.addAttribute("beginIndex", begin);
        model.addAttribute("endIndex", end);
        model.addAttribute("currentIndex", current);

        model.addAttribute("species", species);
        model.addAttribute("ec", ec);

        return "species";
    }

    @ResponseBody
    @RequestMapping(value = FIND_SPECIES_BY_EC, method = RequestMethod.GET)
    public List<Species> findSpeciesByEc(@RequestParam(value = "ec", required = true) String ec) {

        long startTime = System.nanoTime();

        List<Species> species = enzymePortalService.findSpeciesByEcNumber(ec);

        long endTime = System.nanoTime();
        long duration = endTime - startTime;
        long elapsedtime = TimeUnit.SECONDS.convert(duration, TimeUnit.NANOSECONDS);
        LOGGER.warn("Duration :  (" + elapsedtime + " sec)");

        return species.stream().limit(50).collect(Collectors.toList());
    }

    private SearchResults findEnzymesByEc(String ec) {

        SearchResults results = null;
        EnzymeFinder finder = new EnzymeFinder(enzymePortalService, ebeyeRestService);

        SearchParams searchParams = new SearchParams();
        searchParams.setText(ec);//use the ec number here. note ebeye is indexing ep data for ec to be searchable
        searchParams.setType(SearchParams.SearchType.KEYWORD);
        searchParams.setStart(0);
        searchParams.setPrevioustext(ec);//use ec here

        finder.setSearchParams(searchParams);

        results = finder.computeEnzymeSummariesByEc(ec);

        if (results == null) {

            return getEnzymes(finder, searchParams);
        }

        return results;
    }

    private SearchResults getEnzymes(EnzymeFinder finder, SearchParams searchParams) {

        SearchResults results = finder.getEnzymes(searchParams);

        return results;
    }

    private String computeResult(@ModelAttribute("searchModel") SearchModel searchModel,
            @RequestParam(value = "entryid", required = false) String entryID, @RequestParam(value = "entryname", required = false) String entryname,
            Model model, HttpSession session, HttpServletRequest request) {

        String view = "error";

        Map<String, SearchResults> prevSearches
                = getPreviousSearches(session.getServletContext());
        String searchKey = getSearchKey(searchModel.getSearchparams());

        SearchResults results = prevSearches.get(searchKey);

        if (results == null) {
            // New search:
            clearHistory(session);
            results = findEnzymesByEc(entryID);

        }

        if (results != null) {
            cacheSearch(session.getServletContext(), searchKey, results);
            setLastSummaries(session, results.getSummaryentries());
            searchModel.setSearchresults(results);
            applyFilters(searchModel, request);
            model.addAttribute("searchConfig", searchConfig);
            model.addAttribute("searchModel", searchModel);
            model.addAttribute("pagination", getPagination(searchModel));
            clearHistory(session);
            addToHistory(session, searchModel.getSearchparams().getType(),
                    searchKey);
            request.setAttribute("searchTerm", searchModel.getSearchparams().getText());
            view = RESULT;
        }

        return view;
    }

    @ModelAttribute("searchModel")
    public SearchModel searchform(String text) {
        SearchModel searchModelForm = new SearchModel();
        SearchParams searchParams = new SearchParams();
        searchParams.setStart(0);
        searchParams.setText(text);
        searchParams.setType(SearchParams.SearchType.KEYWORD);
        searchModelForm.setSearchparams(searchParams);
        return searchModelForm;
    }

    @RequestMapping(value = BROWSE_ENZYME_CLASSIFICATION, method = RequestMethod.GET)
    public String browseEc(Model model, HttpSession session) {
        clearSelectedEc(session);

        SearchModel searchModelForm = searchform();
        model.addAttribute("searchModel", searchModelForm);
        model.addAttribute(BROWSE_VIDEO, BROWSE_VIDEO);
        return BROWSE_ENZYMES;
    }

    @RequestMapping(value = BROWSE_EC + "/{ec}/{ecname}", method = RequestMethod.GET)
    public String showStaticEc(@ModelAttribute("searchModel") SearchModel searchModel,
            @PathVariable("ec") String ec, @PathVariable("ecname") String ecname,
            Model model, HttpSession session, HttpServletRequest request) throws MalformedURLException, IOException {
        clearSelectedEc(session);
        browseEc(model, session, ecname, null, null, null, ec);
        return EC;

    }

    @RequestMapping(value = BROWSE_EC, method = RequestMethod.GET)
    public String browseEcTree(@ModelAttribute("searchModel") SearchModel searchModel,
            @RequestParam(value = "ec", required = false) String ec, @RequestParam(value = "ecname", required = false) String ecname,
            @RequestParam(value = "subecname", required = false) String subecname,
            @RequestParam(value = "subsubecname", required = false) String subsubecname,
            @RequestParam(value = "entryecname", required = false) String entryecname, Model model, HttpSession session, HttpServletRequest request, Pageable pageable, RedirectAttributes attributes) throws MalformedURLException, IOException {
        model.addAttribute(BROWSE_VIDEO, BROWSE_VIDEO);
        if (ec != null && ec.length() >= 7) {
            model.addAttribute("entryid", ec);
            searchModel = searchform(ec);
            //return computeResult(searchModel, ec, entryecname, model, session, request);
            return searchByEcNumber(searchModel, ec, ecname, subecname, subsubecname, entryecname, model, request, session, pageable, attributes);

        } else {
            browseEc(model, session, ecname, subecname, subsubecname, entryecname, ec);
        }

        return EC;
    }

    private void browseEc(Model model, HttpSession session, String ecname, String sub_ecname, String subsub_ecname, String entry_ecname, String ec) throws MalformedURLException, IOException {

        String intenz_url = String.format("%s/%s.json", INTENZ_URL, ec);
        URL url = new URL(intenz_url);
        try (InputStream is = url.openStream();
                JsonReader rdr = Json.createReader(is)) {

            computeJsonData(rdr, model, session, ecname, sub_ecname, subsub_ecname, entry_ecname, ec);
        }
    }

    /**
     * This method keeps track of the selected enzymes in their hierarchy for
     * the browse enzyme
     *
     * @param session
     * @param s the selected enzyme
     * @param type the position in the hierarchy
     */
    private void addToSelectedEc(HttpSession session, IntenzEnzyme s, String type) {
        @SuppressWarnings("unchecked")
        LinkedList<IntenzEnzyme> history = (LinkedList<IntenzEnzyme>) session.getAttribute(selectedEc);

        if (history == null) {

            history = new LinkedList<>();
            session.setAttribute(selectedEc, history);
        }

        if (!history.isEmpty() && history.contains(s)) {

            if (type.equalsIgnoreCase(ROOT) && history.size() == 2) {
                history.removeLast();

            }
            if (type.equalsIgnoreCase(ROOT) && history.size() == 3) {
                history.removeLast();
                history.removeLast();
                //history.remove(history.size()-1);//same as above

            }
            if (type.equalsIgnoreCase(SUBCLASS) && history.size() == 2) {
                history.removeLast();
                history.add(s);

            }
            if (type.equalsIgnoreCase(SUBCLASS) && history.size() == 3) {
                history.removeLast();

            }

        } else if ((history.isEmpty() || !history.contains(s)) && (history.size() < 3)) {
            history.add(s);

        }
    }

    private void clearSelectedEc(HttpSession session) {
        @SuppressWarnings("unchecked")
        LinkedList<IntenzEnzyme> history = (LinkedList<IntenzEnzyme>) session.getAttribute(selectedEc);
        if (history == null) {
            //history = new ArrayList<String>();
            history = new LinkedList<>();
            session.setAttribute(selectedEc, history);
        } else {
            history.clear();
        }
    }

    private void computeJsonData(JsonReader jsonReader, Model model, HttpSession session, String... ecname) {
        JsonObject jsonObject = jsonReader.readObject();

        IntenzEnzyme root = new IntenzEnzyme();

        String ec = jsonObject.getString(EC_NUMBER);
        //String name = jsonObject.getString(NAME);
        String description = null;

        if (jsonObject.containsKey(DESCRIPTION)) {
            description = jsonObject.getString(DESCRIPTION);

            root.setDescription(description);
        }
        root.setEc(ec);
        root.setName(ecname[0]);
        root.setSubclassName(ecname[1]);
        root.setSubsubclassName(ecname[2]);
        root.setEntryName(ecname[3]);

        //compute the childObject
        if (jsonObject.containsKey(SUBCLASSES)) {

            JsonArray jsonArray = jsonObject.getJsonArray(SUBCLASSES);

            for (JsonObject childObject : jsonArray.getValuesAs(JsonObject.class)) {
                String _ec = null;
                String _name = null;
                String _desc = null;
                _ec = childObject.getString(EC_NUMBER);
                _name = childObject.getString(NAME);

                EnzymeSubclass subclass = new EnzymeSubclass();

                if (childObject.containsKey(DESCRIPTION)) {
                    _desc = childObject.getString(DESCRIPTION);
                    subclass.setDescription(_desc);
                }

                subclass.setEc(_ec);
                subclass.setName(_name);
                root.getChildren().add(subclass);

            }
            addToSelectedEc(session, root, ROOT);
            model.addAttribute("json", root);
        }
        if (jsonObject.containsKey(SUBSUBCLASSES)) {

            JsonArray jsonArray = jsonObject.getJsonArray(SUBSUBCLASSES);

            for (JsonObject childObject : jsonArray.getValuesAs(JsonObject.class)) {
                String _ec = null;
                String _name = null;
                String _desc = null;
                _ec = childObject.getString(EC_NUMBER);
                _name = childObject.getString(NAME);

                EnzymeSubSubclass subsubclass = new EnzymeSubSubclass();

                if (childObject.containsKey(DESCRIPTION)) {
                    _desc = childObject.getString(DESCRIPTION);

                    subsubclass.setDescription(_desc);
                }

                subsubclass.setEc(_ec);
                subsubclass.setName(_name);

                root.getSubSubclasses().add(subsubclass);

            }

            model.addAttribute("json", root);
            addToSelectedEc(session, root, SUBCLASS);
        }
        if (jsonObject.containsKey(ENTRIES)) {

            JsonArray jsonArray = jsonObject.getJsonArray(ENTRIES);

            for (JsonObject childObject : jsonArray.getValuesAs(JsonObject.class)) {
                String _ec = null;
                String _name = null;
                String _desc = null;
                _ec = childObject.getString(EC_NUMBER);
                _name = childObject.getString(NAME);

                EnzymeEntry entries = new EnzymeEntry();
                if (childObject.containsKey(DESCRIPTION)) {
                    _desc = childObject.getString(DESCRIPTION);

                    entries.setDescription(_desc);
                }

                entries.setEc(_ec);
                entries.setName(_name);
                root.setEc(ecname[4]);
                root.getEntries().add(entries);

            }

            model.addAttribute("json", root);
            addToSelectedEc(session, root, SUBSUBCLASS);
        }

    }


    @RequestMapping(value = SEARCH_ENZYMES, method = RequestMethod.GET)
    public String searchByEcNumber(@ModelAttribute("searchModel") SearchModel searchModel,
            @RequestParam(value = "ec", required = false) String ec, @RequestParam(value = "ecname", required = false) String ecname,
            @RequestParam(value = "subecname", required = false) String subecname,
            @RequestParam(value = "subsubecname", required = false) String subsubecname,
            @RequestParam(value = "entryecname", required = false) String entryecname,
            Model model, HttpServletRequest request, HttpSession session, Pageable pageable, RedirectAttributes attributes) {

        pageable = new PageRequest(0, SEARCH_PAGESIZE, Sort.Direction.ASC, "entryType", "function");

        long startTime = System.nanoTime();
        Page<UniprotEntry> page = this.enzymePortalService.findEnzymesByEcNumber(ec, pageable);
         //Page<UniprotEntry> page = this.enzymePortalService.findEnzymeViewByEc(ec, pageable);
        long endTime = System.nanoTime();
        long duration = endTime - startTime;

        long elapsedtime = TimeUnit.SECONDS.convert(duration, TimeUnit.NANOSECONDS);
        LOGGER.warn("findEnzymesByEcNumber took  :  (" + elapsedtime + " sec)");

        long startTime1 = System.nanoTime();
        List<Species> species = enzymePortalService.findSpeciesByEcNumber(ec);
        List<Compound> compouds = enzymePortalService.findCompoundsByEcNumber(ec);
        List<Disease> diseases = enzymePortalService.findDiseasesByEcNumber(ec);

        List<EcNumber> enzymeFamilies = enzymePortalService.findEnzymeFamiliesByEcNumber(ec);

        long endTime1 = System.nanoTime();
        long duration1 = endTime1 - startTime1;

        long elapsedtime1 = TimeUnit.SECONDS.convert(duration1, TimeUnit.NANOSECONDS);
        LOGGER.warn("findEnzymesByEcNumber Filter Facets took  :  (" + elapsedtime1 + " sec)");

        SearchParams searchParams = searchModel.getSearchparams();
        searchParams.setStart(0);
        searchParams.setType(SearchParams.SearchType.KEYWORD);
        searchParams.setText(ec);
        searchParams.setPrevioustext("");
        searchParams.setSize(SEARCH_PAGESIZE);
        searchModel.setSearchparams(searchParams);

        List<UniprotEntry> result = page.getContent();//.stream().map(EnzymePortal::new).distinct().map(EnzymePortal::unwrapProtein).filter(Objects::nonNull).collect(Collectors.toList());

        int current = page.getNumber() + 1;
        int begin = Math.max(1, current - 5);
        int end = Math.min(begin + 10, page.getTotalPages());

        model.addAttribute("page", page);
        model.addAttribute("beginIndex", begin);
        model.addAttribute("endIndex", end);
        model.addAttribute("currentIndex", current);

        model.addAttribute("ecname", entryecname);
        model.addAttribute("ec", ec);

        // model.addAttribute("summaryEntries", result);
        SearchResults searchResults = new SearchResults();

        searchResults.setTotalfound(page.getTotalElements());
        SearchFilters filters = new SearchFilters();
        //Set<Species> speciesFilter = species.stream().collect(Collectors.toSet());
        List<Species> speciesFacets = applySpeciesFilter(species);
        filters.setSpecies(speciesFacets);
        filters.setCompounds(compouds);
        filters.setEcNumbers(enzymeFamilies);
        filters.setDiseases(diseases);

        searchResults.setSearchfilters(filters);
        searchResults.setSummaryentries(result);

        searchModel.setSearchresults(searchResults);

        String searchKey = getSearchKey(searchModel.getSearchparams());

        cacheSearch(session.getServletContext(), searchKey, searchResults);
        setLastSummaries(session, searchResults.getSummaryentries());
        clearHistory(session);

        addToHistory(session, searchModel.getSearchparams().getType(),
                searchKey);
        model.addAttribute("searchModel", searchModel);
        model.addAttribute("searchConfig", searchConfig);

        model.addAttribute("searchFilter", filters);
        model.addAttribute(BROWSE_VIDEO, BROWSE_VIDEO);
        request.setAttribute("searchTerm", searchModel.getSearchparams().getText());

        return RESULT;
    }

    @RequestMapping(value = SEARCH_ENZYMES + "/page={pageNumber}", method = RequestMethod.GET)
    public String searchByEcNumberPaginated(@PathVariable Integer pageNumber, @ModelAttribute("searchModel") SearchModel searchModel,
            @RequestParam(value = "ec", required = true) String ec, @RequestParam(value = "ecname", required = false) String ecname,
            @RequestParam(value = "subecname", required = false) String subecname,
            @RequestParam(value = "subsubecname", required = false) String subsubecname,
            @RequestParam(value = "entryecname", required = false) String entryecname,
            Model model, HttpServletRequest request, HttpSession session, RedirectAttributes attributes) {

        if (pageNumber < 1) {
            pageNumber = 1;
        }

        Pageable pageable = new PageRequest(pageNumber - 1, SEARCH_PAGESIZE, Sort.Direction.ASC, "entryType", "function");

        Page<UniprotEntry> page = this.enzymePortalService.findEnzymesByEcNumber(ec, pageable);
        //Page<UniprotEntry> page = this.enzymePortalService.findEnzymeViewByEc(ec, pageable);

        List<Species> species = enzymePortalService.findSpeciesByEcNumber(ec);
        List<Compound> compouds = enzymePortalService.findCompoundsByEcNumber(ec);
        List<Disease> diseases = enzymePortalService.findDiseasesByEcNumber(ec);

        List<EcNumber> enzymeFamilies = enzymePortalService.findEnzymeFamiliesByEcNumber(ec);

        SearchParams searchParams = searchModel.getSearchparams();
        searchParams.setStart(0);
        searchParams.setType(SearchParams.SearchType.KEYWORD);
        searchParams.setText(ec);
        searchParams.setSize(SEARCH_PAGESIZE);
        searchParams.setPrevioustext("");
        searchModel.setSearchparams(searchParams);

        List<UniprotEntry> result = page.getContent();

        int current = page.getNumber() + 1;
        int begin = Math.max(1, current - 5);
        int end = Math.min(begin + 10, page.getTotalPages());

        model.addAttribute("page", page);
        model.addAttribute("beginIndex", begin);
        model.addAttribute("endIndex", end);
        model.addAttribute("currentIndex", current);

        model.addAttribute("ecname", entryecname);
        model.addAttribute("ec", ec);

        // model.addAttribute("summaryEntries", result);
        SearchResults searchResults = new SearchResults();

        searchResults.setTotalfound(page.getTotalElements());
        SearchFilters filters = new SearchFilters();
        //Set<Species> speciesFilter = species.stream().collect(Collectors.toSet());
        List<Species> speciesFacets = applySpeciesFilter(species);
        filters.setSpecies(speciesFacets);
        filters.setCompounds(compouds);
        filters.setEcNumbers(enzymeFamilies);
        filters.setDiseases(diseases);

        searchResults.setSearchfilters(filters);
        searchResults.setSummaryentries(result);

        searchModel.setSearchresults(searchResults);

        String searchKey = getSearchKey(searchModel.getSearchparams());

        cacheSearch(session.getServletContext(), searchKey, searchResults);
        setLastSummaries(session, searchResults.getSummaryentries());
        clearHistory(session);

        addToHistory(session, searchModel.getSearchparams().getType(),
                searchKey);
        model.addAttribute("searchModel", searchModel);
        model.addAttribute("searchConfig", searchConfig);

        model.addAttribute("searchFilter", filters);
        model.addAttribute(BROWSE_VIDEO, BROWSE_VIDEO);
        request.setAttribute("searchTerm", searchModel.getSearchparams().getText());

        return RESULT;
    }


    @RequestMapping(value = SEARCH_ENZYMES, method = RequestMethod.POST)
    public String searchEnzymesByEcPost(@ModelAttribute("searchModel") SearchModel searchModel,
            @RequestParam(value = "ec", required = false) String ec, @RequestParam(value = "ecname", required = false) String ecname,
            @RequestParam(value = "subecname", required = false) String subecname,
            @RequestParam(value = "subsubecname", required = false) String subsubecname,
            @RequestParam(value = "entryecname", required = false) String entryecname,
            Model model, HttpServletRequest request, HttpSession session, Pageable pageable, RedirectAttributes attributes) {

        model.addAttribute("entryid", ec);
        model.addAttribute("entryname", entryecname);
        return searchByEcNumber(searchModel, ec, ecname, subecname, subsubecname, entryecname, model, request, session, pageable, attributes);

    }

    private List<Species> applySpeciesFilter(List<Species> uniqueSpecies) {
        //  String[] commonSpecie = {"HUMAN", "MOUSE", "RAT", "Fruit fly", "WORM", "Yeast", "ECOLI"};
        // CommonSpecies [] commonSpecie = {"Homo sapiens","Mus musculus","Rattus norvegicus", "Drosophila melanogaster","Saccharomyces cerevisiae"};
        // List<String> commonSpecieList = Arrays.asList(commonSpecie);
        List<String> commonSpecieList = new ArrayList<>();
        for (CommonSpecies commonSpecies : CommonSpecies.values()) {
            commonSpecieList.add(commonSpecies.getScientificName());
        }

        Map<Integer, Species> priorityMapper = new TreeMap<>();

        AtomicInteger key = new AtomicInteger(50);
        AtomicInteger customKey = new AtomicInteger(6);

        uniqueSpecies.stream().forEach((sp) -> {
            if (commonSpecieList.contains(sp.getScientificname().split("\\(")[0].trim())) {
                // HUMAN, MOUSE, RAT, Fly, WORM, Yeast, ECOLI 
                // "Homo sapiens","Mus musculus","Rattus norvegicus", "Drosophila melanogaster","WORM","Saccharomyces cerevisiae","ECOLI"
                if (sp.getScientificname().equalsIgnoreCase(CommonSpecies.HUMAN.getScientificName())) {
                    priorityMapper.put(1, sp);
                } else if (sp.getScientificname().equalsIgnoreCase(CommonSpecies.MOUSE.getScientificName())) {
                    priorityMapper.put(2, sp);
                } else if (sp.getScientificname().equalsIgnoreCase(CommonSpecies.RAT.getScientificName())) {
                    priorityMapper.put(3, sp);
                } else if (sp.getScientificname().equalsIgnoreCase(CommonSpecies.FRUIT_FLY.getScientificName())) {
                    priorityMapper.put(4, sp);
                } else if (sp.getScientificname().equalsIgnoreCase(CommonSpecies.WORM.getScientificName())) {
                    priorityMapper.put(5, sp);
                } else if (sp.getScientificname().equalsIgnoreCase(CommonSpecies.ECOLI.getScientificName())) {
                    priorityMapper.put(6, sp);
                } else if (sp.getScientificname().split("\\(")[0].trim().equalsIgnoreCase(CommonSpecies.BAKER_YEAST.getScientificName())) {
                    priorityMapper.put(customKey.getAndIncrement(), sp);

                }
            } else {

                priorityMapper.put(key.getAndIncrement(), sp);

            }
        });

        List<Species> speciesFilters = new LinkedList<>();
        priorityMapper.entrySet().stream().forEach(map -> {
            speciesFilters.add(map.getValue());
        });

        return speciesFilters;
    }

}

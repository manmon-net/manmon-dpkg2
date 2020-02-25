package net.manmon.pkg.loader;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class PkgRestController {
    @Autowired
    private JdbcTemplate jdbcTemplate;

    @GetMapping("/load")
    public boolean load() throws Exception {
        new CacheLoader(jdbcTemplate.getDataSource().getConnection()).loadCachedData();
        PkgLoader loader = new PkgLoader(jdbcTemplate);
        loader.loadPkgUpstreams(true, null);

        return true;
    }

    @GetMapping("/vers")
    public boolean vers() throws Exception{
        new PkgVersionSorter(jdbcTemplate.getDataSource().getConnection()).sort();
        return true;
    }

}

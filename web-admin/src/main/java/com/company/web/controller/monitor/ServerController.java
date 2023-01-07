package com.company.web.controller.monitor;

import com.company.common.core.domain.R;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import com.company.framework.web.domain.Server;

/**
 * 服务器监控
 *
 */
@RestController
@RequestMapping("/monitor/server")
public class ServerController {

    @PreAuthorize("@ss.hasPermi('monitor:server:list')")
    @GetMapping()
    public R getInfo() throws Exception {
        Server server = new Server();
        server.copyTo();
        return R.successWithData(server);
    }

}

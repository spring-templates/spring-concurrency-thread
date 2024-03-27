package com.thread.concurrency.async.controller;

import com.thread.concurrency.async.service.AsyncService;
import org.springframework.stereotype.Controller;
@Controller
public class AsyncController {
    private final AsyncService asyncService;

    public AsyncController(AsyncService asyncService) {
        this.asyncService = asyncService;
    }
}

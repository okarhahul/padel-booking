package com.padelarena.padel_boking.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Component
public class HoldScheduler {

    private static final Logger log = LoggerFactory.getLogger(HoldScheduler.class);

    private final HoldCleanupService cleanupService;

    public HoldScheduler(HoldCleanupService cleanupService) {
        this.cleanupService = cleanupService;
    }

    // Dijalankan berulang, 30 detik setelah putaran sebelumnya selesai
    @Scheduled(fixedDelay = 30_000)
    public void releaseExpiredHolds() {
        int jumlah = cleanupService.releaseExpired();
        if (jumlah > 0) {
            log.info("Melepas {} pesanan kedaluwarsa", jumlah);
        }
    }
}
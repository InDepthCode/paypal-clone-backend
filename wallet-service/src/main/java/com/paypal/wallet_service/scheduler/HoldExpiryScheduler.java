package com.paypal.wallet_service.scheduler;


import com.paypal.wallet_service.entity.WalletHold;
import com.paypal.wallet_service.repository.WalletHoldRepository;
import com.paypal.wallet_service.service.WalletService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.List;

@Component
public class HoldExpiryScheduler {

    @Autowired
    private WalletHoldRepository walletHoldRepository;


    @Autowired
    private WalletService walletService;


    // this runs every minute by default
    @Scheduled(fixedRateString = "${wallet.hold.expiry.scan-rate-ms:60000}")
    public void expireOldHolds(){

        // current time stamp
        LocalDateTime now = LocalDateTime.now();

        // simple: fetch expired active holds (OK for small data sets)
        // we are finding out the wallets that are currently active in this time stamp
        List<WalletHold> expired = walletHoldRepository.findByStatusAndExpiresAtBefore("Active", now);

        for(WalletHold hold: expired){
            // if the current hold is expired or not
            String ref = hold.getHoldReference();

            try {
                // reuse existing release logic (locks, audit, idempotency)
                walletService.releaseHold(ref);
                System.out.println("🔄 Expired hold released: " + ref);
            } catch (Exception e) {
                // log and continue - don't block the sweep
                System.err.println("❌ Failed to release expired hold " + ref + ": " + e.getMessage());
            }
        }



    }
}

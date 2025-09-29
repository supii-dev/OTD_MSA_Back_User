package com.otd.otd_challenge.application.challenge;

import com.otd.configuration.enumcode.model.EnumChallengeRole;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class TierService {
    public EnumChallengeRole checkTierUp(EnumChallengeRole myRole, int xp) {
        switch (myRole) {
            case BRONZE:
                if (xp >= 500) return EnumChallengeRole.SILVER;
                break;
            case SILVER:
                if (xp >= 1000) return EnumChallengeRole.DIAMOND;
                if (xp >= 500) return EnumChallengeRole.GOLD;
                break;
            case GOLD:
                if (xp >= 1500) return EnumChallengeRole.DIAMOND;
                if (xp >= 1000) return EnumChallengeRole.DIAMOND;
                if (xp >= 500) return EnumChallengeRole.DIAMOND;
                break;
            case DIAMOND:
                return EnumChallengeRole.DIAMOND;
        }
        return myRole;
    }
}

package org.starcoin.airdrop.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.HashMap;
import java.util.Map;

@Configuration
public class StarcoinChainConfig {

    @Bean
    public ChainSettings chainSettings(@Value("${starcoin.chain-id}") Integer chainId,
                                       @Value("${starcoin.network}") String network) {
        return new ChainSettings(chainId, network);
    }

    public static class ChainSettings {
        private static final Map<String, Integer> KNOWN_NETWORK_CHAIN_ID_MAP;

        static {
            Map<String, Integer> m = new HashMap<>();
            m.put("barnard", 251);
            m.put("main", 1);
            KNOWN_NETWORK_CHAIN_ID_MAP = m;
        }

        private final Integer chainId;

        private final String network;

        public ChainSettings(Integer chainId, String network) {
            if (KNOWN_NETWORK_CHAIN_ID_MAP.containsKey(network)
                    && !KNOWN_NETWORK_CHAIN_ID_MAP.get(network).equals(chainId)) {
                throw new RuntimeException("Wrong network and chain-id pair: '" + network + "' / " + chainId);
            }
            this.chainId = chainId;
            this.network = network;
        }

        public Integer getChainId() {
            return chainId;
        }

        public String getNetwork() {
            return network;
        }
    }
}

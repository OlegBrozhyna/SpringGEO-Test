package ua.kiev.prog.retrievers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.CacheManager;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import ua.kiev.prog.json.Rate;

@Service
public class RateRetriever {

    private static final String KEY = "39mXC3PG1jwdjF20Wcu26OrDZn9og7CS";
    private static final String URL = "https://api.apilayer.com/fixer/latest?symbols=UAH&base=EUR";

    @Autowired
    private CacheManager cacheManager;

    @Cacheable(value = "rates", key = "'rate'")
    public Rate getRate() {
        HttpHeaders headers = new HttpHeaders();
        headers.set("apikey", KEY);
        HttpEntity<String> entity = new HttpEntity<>("parameters", headers);
        ResponseEntity<Rate> response = new RestTemplate().exchange(URL, HttpMethod.GET, entity, Rate.class);
        return response.getBody();
    }

    @Scheduled(fixedRate = 60000)
    public void updateRate() {
        cacheManager.getCache("rates").evict("rate");
        getRate();
    }
}

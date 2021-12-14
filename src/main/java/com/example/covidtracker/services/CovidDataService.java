package com.example.covidtracker.services;

import com.example.covidtracker.models.RegionalStat;
import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVRecord;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import java.io.IOException;
import java.io.StringReader;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.ArrayList;
import java.util.List;

@Service
public class CovidDataService {

    private  static String COVID_DATA_URL="https://raw.githubusercontent.com/CSSEGISandData/COVID-19/master/csse_covid_19_data/csse_covid_19_time_series/time_series_covid19_confirmed_global.csv";
    private List<RegionalStat> allStats = new ArrayList<>();

    @PostConstruct
    @Scheduled(cron = "0 0 * * * ?")
    public void fetchCovidData() throws IOException, InterruptedException {
        List<RegionalStat> newStats = new ArrayList<>();
        HttpClient client = HttpClient.newHttpClient();
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(COVID_DATA_URL))
                .build();
        HttpResponse<String> httpResponse = client.send(request, HttpResponse.BodyHandlers.ofString());
        StringReader csvBodyReader = new StringReader(httpResponse.body());
        Iterable<CSVRecord> records = CSVFormat.DEFAULT.withFirstRecordAsHeader().parse(csvBodyReader);
        for (CSVRecord record : records) {
            RegionalStat regionalStat = new RegionalStat();
            regionalStat.setProvinceOrState(record.get("Province/State"));
            regionalStat.setCountryOrRegion(record.get("Country/Region"));
            regionalStat.setLatestTotalCases(Integer.parseInt(record.get(record.size()-1)));
            newStats.add(regionalStat);
        }
        this.allStats = newStats;
    }

}

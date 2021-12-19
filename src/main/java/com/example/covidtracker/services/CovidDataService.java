package com.example.covidtracker.services;

import com.example.covidtracker.models.RegionalStat;
import com.example.covidtracker.models.StateRecord;
import com.example.covidtracker.repository.StateRecordRepository;
import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVParser;
import org.apache.commons.csv.CSVRecord;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import java.io.IOException;
import java.io.StringReader;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.LocalDate;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.ArrayList;
import java.util.List;

@Service
public class CovidDataService {
    private static String COVID_DATA_URL_2 = "https://raw.githubusercontent.com/nytimes/covid-19-data/master/us-states.csv";

    @Autowired
    private StateRecordRepository stateRecordRepository;
    private List<StateRecord> allRecord = new ArrayList<>();


    //-----------------------------------------------------

    private static String COVID_DATA_URL = "https://raw.githubusercontent.com/CSSEGISandData/COVID-19/master/csse_covid_19_data/csse_covid_19_time_series/time_series_covid19_confirmed_US.csv";

    private List<RegionalStat> allStats = new ArrayList<>();
    private ZonedDateTime updateTime = ZonedDateTime.now(ZoneId.of("America/New_York"));

    public List<RegionalStat> getAllStats() {
        return allStats;
    }

    public ZonedDateTime getUpdateTime() {
        return updateTime;
    }

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
            regionalStat.setCounty(record.get("Admin2"));
            regionalStat.setState(record.get("Province_State"));
            int latestCases = Integer.parseInt(record.get(record.size() - 1));
            int prevCases = Integer.parseInt(record.get(record.size() - 2));
            regionalStat.setLatestTotalCases(latestCases);
            regionalStat.setDiffFromPrevDay(latestCases - prevCases);
            newStats.add(regionalStat);
        }
        this.allStats = newStats;
        this.updateTime = ZonedDateTime.now(ZoneId.of("America/New_York"));
        ;
    }

    @PostConstruct
    @Scheduled(cron = "0 0 * * * ?")
    public void updateCovidData() throws IOException, InterruptedException {
        List<RegionalStat> newStats = new ArrayList<>();
        HttpClient client = HttpClient.newHttpClient();
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(COVID_DATA_URL_2))
                .build();
        HttpResponse<String> httpResponse = client.send(request, HttpResponse.BodyHandlers.ofString());
        StringReader csvBodyReader = new StringReader(httpResponse.body());
        CSVParser records = CSVFormat.DEFAULT.withFirstRecordAsHeader().parse(csvBodyReader);
        allRecord = stateRecordRepository.findAll();
        int i = 1;
        for (CSVRecord record : records) {
            if (i > allRecord.size()) {
                StateRecord stateRecord = new StateRecord();
                stateRecord.setDate(LocalDate.parse(record.get("date")));
                stateRecord.setState(record.get("state"));
                stateRecord.setCases(Integer.valueOf(record.get("cases")));
                stateRecord.setDeath(Integer.valueOf(record.get("deaths")));
                stateRecordRepository.save(stateRecord);
            }
            i++;
        }
    }

}

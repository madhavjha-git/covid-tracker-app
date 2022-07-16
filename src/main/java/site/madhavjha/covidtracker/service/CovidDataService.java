package site.madhavjha.covidtracker.service;

import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVRecord;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import site.madhavjha.covidtracker.model.CovidStat;

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

    private static Logger LOGGER=LoggerFactory.getLogger(CovidDataService.class);
    private static String COVID_DATA_URL = "https://raw.githubusercontent.com/CSSEGISandData/COVID-19/master/csse_covid_19_data/csse_covid_19_time_series/time_series_covid19_confirmed_global.csv";

    private List<CovidStat> stats = new ArrayList<>();

    @Scheduled(cron = "* * 1 * * *")
    @PostConstruct
    public void fetchCovidData() throws IOException, InterruptedException {
        List<CovidStat> latestStats = new ArrayList<>();
        HttpClient httpClient = HttpClient.newHttpClient();
        HttpRequest httpRequest = HttpRequest.newBuilder().uri(URI.create(COVID_DATA_URL)).build();

        LOGGER.info("Fetching latest covid data from web!!");
        HttpResponse<String> httpResponse = httpClient.send(httpRequest, HttpResponse.BodyHandlers.ofString());
        StringReader in = new StringReader(httpResponse.body());

        CSVFormat csvFormat = CSVFormat.DEFAULT.builder().setHeader().build();
        Iterable<CSVRecord> records = csvFormat.parse(in);
        for (CSVRecord record : records) {
            CovidStat stat = new CovidStat();
            stat.setState(record.get("Province/State"));
            stat.setCountry(record.get("Country/Region"));

            long latestTotalCases = Long.parseLong(record.get(record.size() - 1));
            long prevDayTotalCases = Long.parseLong(record.get(record.size() - 2));

            stat.setLatestTotalCases(latestTotalCases);
            stat.setDiffFromPrevDay(latestTotalCases - prevDayTotalCases);

            latestStats.add(stat);
        }

        //Assign latest stats
        LOGGER.info("Total Records fetched : {}",latestStats.size());
        stats = latestStats;
    }

    public List<CovidStat> getStats() {
        return stats;
    }
}

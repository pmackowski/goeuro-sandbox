package pmackowski.grpc.client.rest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import pmackowski.grpc.client.JourneyClientService;
import pmackowski.grpc.model.JourneyOuterClass.Journey;
import pmackowski.grpc.model.JourneyOuterClass.JourneyQuery;

@RestController
public class TestJourneyController {

    @Autowired
    private JourneyClientService journeyClientService;

    @RequestMapping(value = "/journey")
    public String journey(@RequestParam(value = "departure") String departure,
                          @RequestParam(value = "arrival") String arrival) {

        JourneyQuery journeyQuery = JourneyQuery.newBuilder()
                .setDeparture(departure)
                .setArrival(arrival)
                .build();

        Journey journey = journeyClientService.query(journeyQuery);
        return journey.toString();
    }

}

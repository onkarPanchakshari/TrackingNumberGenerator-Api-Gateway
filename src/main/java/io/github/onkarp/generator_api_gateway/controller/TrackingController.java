package io.github.onkarp.generator_api_gateway.controller;




import io.github.onkarp.generator_api_gateway.dto.NotificationRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.RestTemplate;

import java.net.URI;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;

@RestController
@RequestMapping("/tracking")
public class TrackingController {

    @Autowired
    private RestTemplate restTemplate;

    private final String TRACKING_API = "https://trackingnumbergenerator-api.onrender.com/api/next-tracking-number";
    private final String NOTIFICATION_API = "http://localhost:8080/notify";

    @GetMapping("/generate")
    public ResponseEntity<String> generateTracking(
            @RequestParam String originCountryId,
            @RequestParam String destinationCountryId,
            @RequestParam double weight,
            @RequestParam String customerId,
            @RequestParam String customerName,
            @RequestParam String customerSlug,
            @RequestParam String customerEmail
    ) {

        try {
            //Build URL for Tracking API with query params
            String url = TRACKING_API +
                    "?originCountryId=" + URLEncoder.encode(originCountryId, StandardCharsets.UTF_8) +
                    "&destinationCountryId=" + URLEncoder.encode(destinationCountryId, StandardCharsets.UTF_8) +
                    "&weight=" + weight +
                    "&customerId=" + URLEncoder.encode(customerId, StandardCharsets.UTF_8) +
                    "&customerName=" + URLEncoder.encode(customerName, StandardCharsets.UTF_8)+
                    "&customerSlug=" + URLEncoder.encode(customerSlug, StandardCharsets.UTF_8);

            //  Call Tracking API
            String trackingNumber = restTemplate.getForObject(new URI(url), String.class);

            // Call Notification Service
            NotificationRequest notifReq = new NotificationRequest(
                    trackingNumber,
                    customerName,
                    customerSlug,
                    customerEmail
            );

            restTemplate.postForObject(new URI(NOTIFICATION_API), notifReq, String.class);

            // Return response to client
            return ResponseEntity.ok("Tracking Number: " + trackingNumber + " | Notification sent to " + customerEmail);

        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(500).body("Error: " + e.getMessage());
        }
    }
}


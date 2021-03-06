package com.BestBot.RESTController;

import com.BestBot.ConfigDiscord.MessageSenderJDA;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.springframework.boot.configurationprocessor.json.JSONObject;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.Base64;

@RestController
@RequestMapping("/CNC")
public class CNCController {

    private MessageSenderJDA messageSenderJDA;

    private String newCNCUrl;

    public CNCController(MessageSenderJDA messageSenderJDA) {
        this.messageSenderJDA = messageSenderJDA;
        this.newCNCUrl = "http://localhost:8085/skynet/set0";
    }

    public void sendToCNC(String channelId, String message){

        CloseableHttpClient client = HttpClients.createDefault();
        HttpPost post = new HttpPost(newCNCUrl);
        String encodedStr = Base64.getEncoder().encodeToString(message.getBytes(StandardCharsets.UTF_8));

        ObjectMapper objectMapper = new ObjectMapper();
        ObjectNode objectNode = objectMapper.createObjectNode()
                .put("channelID", channelId)
                .put("messageBody", encodedStr);

        try{
            post.setEntity(new StringEntity(objectNode.toString()));
            CloseableHttpResponse response = client.execute(post);
            response.close();
        }catch (IOException e){
            e.printStackTrace();
        }
}

    @PostMapping(path = "/skynet/set0")
    public void getData(@RequestBody String string) {

        ObjectMapper objectMapper = new ObjectMapper();
        JsonNode jsonNode = null;
        try {
            jsonNode = objectMapper.readTree(string);
        } catch (IOException e) {
            e.printStackTrace();
        }
        String response = new String(Base64.getDecoder().decode(jsonNode.get("messageBody").asText()));

        String channelID = jsonNode.get("channelID").asText();
        messageSenderJDA.getJDA()
                .getTextChannelById(channelID != null ? channelID : "834814097157390377")
                .sendMessage(response)
                .queue();
    }

    @PostMapping(path = "/skynet/setNewCNCUrl")
    public void setNewCNCUrl(@RequestBody String string) {

        ObjectMapper objectMapper = new ObjectMapper();
        JsonNode jsonNode = null;
        try {
            jsonNode = objectMapper.readTree(string);
        } catch (IOException e) {
            e.printStackTrace();
        }
        newCNCUrl = new String(Base64.getDecoder().decode(jsonNode.get("newCNCUrl").asText()));

        messageSenderJDA.getJDA()
                .getTextChannelById("834814097157390377")
                .sendMessage("new Url to POST: " + newCNCUrl)
                .queue();
    }
}

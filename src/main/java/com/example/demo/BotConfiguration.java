package com.example.demo;

import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.JDABuilder;
import net.dv8tion.jda.api.events.Event;
import net.dv8tion.jda.api.requests.GatewayIntent;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import javax.security.auth.login.LoginException;
import java.util.List;

@Configuration
public class BotConfiguration {

    @Value("${token}")
    private String token;

    @Bean
    public <T extends Event> JDA gatewayDiscordClient(List<EventListener<T>> eventListeners) throws LoginException, InterruptedException {
        JDA jda = JDABuilder.create(token, GatewayIntent.GUILD_MESSAGES, GatewayIntent.DIRECT_MESSAGES)
                .build()
                .awaitReady();


        for(EventListener<T> listener : eventListeners) {
           jda.addEventListener(listener);
            System.out.println("ololo3");
        }

        System.out.println("ololo1");
        System.out.println(jda.getRegisteredListeners());

        return jda;

    }
}
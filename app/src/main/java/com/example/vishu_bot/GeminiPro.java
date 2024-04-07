package com.example.vishu_bot;

import com.google.ai.client.generativeai.GenerativeModel;
import com.google.ai.client.generativeai.java.GenerativeModelFutures;
import com.google.ai.client.generativeai.type.BlockThreshold;
import com.google.ai.client.generativeai.type.Content;
import com.google.ai.client.generativeai.type.GenerateContentResponse;
import com.google.ai.client.generativeai.type.GenerationConfig;
import com.google.ai.client.generativeai.type.HarmCategory;
import com.google.ai.client.generativeai.type.SafetySetting;
import com.google.common.util.concurrent.FutureCallback;
import com.google.common.util.concurrent.Futures;
import com.google.common.util.concurrent.ListenableFuture;

import java.util.Collections;
import java.util.concurrent.Executor;

public class GeminiPro {
    public void getResponse(String query, com.example.vishu_bot.ResponseCallback callback){
        GenerativeModelFutures model = getModel();

        Content content = new Content.Builder().addText(query).build();
        Executor executor = Runnable::run;

        ListenableFuture<GenerateContentResponse> response = model.generateContent(content);
        Futures.addCallback(response, new FutureCallback<GenerateContentResponse>() {
            @Override
            public void onSuccess(GenerateContentResponse result) {
                String resultText = result.getText();
                callback.onResponse(resultText);
            }

            @Override
            public void onFailure(Throwable t) {
                t.printStackTrace();
                callback.onError(t);
            }
        },executor);

    }
    private GenerativeModelFutures getModel(){
        String apikey = BuildConfig.apikey;

        SafetySetting harassmentSafety = new SafetySetting(HarmCategory.HARASSMENT, BlockThreshold.ONLY_HIGH);

        GenerationConfig.Builder configbuilder = new GenerationConfig.Builder();
        configbuilder.temperature = 0.9f;
        configbuilder.topK = 16;
        configbuilder.topP = 0.1f;
        GenerationConfig generationConfig = configbuilder.build();

        GenerativeModel gn = new GenerativeModel(
                "gemini-pro",
                apikey,
                generationConfig,
                Collections.singletonList(harassmentSafety)
        );
        return GenerativeModelFutures.from(gn);
    }
}

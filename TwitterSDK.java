///usr/bin/env jbang "$0" "$@" ; exit $?

//DEPS info.picocli:picocli:4.6.3
//DEPS com.twitter:twitter-api-java-sdk:1.1.4

import com.google.common.reflect.TypeToken;
import com.twitter.clientlib.ApiException;
import com.twitter.clientlib.JSON;
import com.twitter.clientlib.TwitterCredentialsBearer;
import com.twitter.clientlib.api.TwitterApi;
import com.twitter.clientlib.model.StreamingTweet;
import picocli.CommandLine;
import picocli.CommandLine.Command;
import picocli.CommandLine.Option;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.lang.reflect.Type;
import java.util.concurrent.Callable;

@Command(name = "TwitterSDK", mixinStandardHelpOptions = true, version = "TwitterSDK 0.1",
        description = "TwitterSDK made with jbang")
class TwitterSDK implements Callable<Integer> {

    @Option(names = "--bearer-token",
            description = "bearerToken",
            defaultValue = "${TWITTER_BEARER_TOKEN}")
    private String bearerToken;

    public static void main(String... args) {
        int exitCode = new CommandLine(new TwitterSDK()).execute(args);
        System.exit(exitCode);
    }

    @Override
    public Integer call() throws Exception { // your business logic goes here...
        System.out.println("bearerToken = " + bearerToken);
        TwitterApi apiInstance = new TwitterApi();
        apiInstance.setTwitterCredentials(new TwitterCredentialsBearer(bearerToken));

        try {
            InputStream result = apiInstance.tweets().sampleStream(null,
                    null,
                    null,
                    null,
                    null,
                    null,
                    null);
            try {
                JSON json = new JSON();
                Type localVarReturnType = new TypeToken<StreamingTweet>() {
                }.getType();
                BufferedReader reader = new BufferedReader(new InputStreamReader(result));
                String line = reader.readLine();
                while (line != null) {
                    System.out.println(json.getGson().fromJson(line, localVarReturnType).toString());
                    line = reader.readLine();
                }
            } catch (Exception e) {
                e.printStackTrace();
                System.out.println(e);
            }
        } catch (ApiException e) {
            handleApiException(e);
        }
        return 0;
    }

    private static void handleApiException(ApiException e) {
        System.err.println("Exception when calling TweetsApi#sampleStream");
        System.err.println("Status code: " + e.getCode());
        System.err.println("Reason: " + e.getResponseBody());
        System.err.println("Response headers: " + e.getResponseHeaders());
        e.printStackTrace();
    }
}

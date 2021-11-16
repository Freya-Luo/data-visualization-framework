### Domain

`Data Plugins` provide a list of `text` fields with `time_stamp` and a `locate_at` field. The possible `Data Plugins` would be implemented as follows:

-   `TwitterPlugin`
    -   Fetches a list of Twiiter Trends which happen at a specific location and within a specific period of time using the Twitter API
-   `NewsPlugin`
    -   Fetches a list News Articles of a country that were published within a period of time with News API

The framework sends a list of `text` along with `time_stamp` and a `locate_at` data input to the `Visualization Plugins`, which could present the following features:

-   Bar charts showing the magnitude of positive`Trends/Events` over time
-   Pie Charts displaying the relative number of postive and negative `Trends/Events` within a period of time

The framework processes data from the above fields and analyze the text sentiment from the given `text` fields in `core`, providing statuses to the `gui` for rendering.

### Project Structure

```java
/framework
    /core
        /FrameWork.java
        /AnalyzeSentiment.java
    /gui
        /Status.java
    /plugins
        /Plugin.java
/plugin
    /dataplugin
        DataPlugin.java
        /twitterplugin
            /TwitterPlugin.java
        /newsplugin
            /NewsPlugin.java
    /visualplugin
        VisualPlugin.java
        /barplugin
            /BarPlugin.java
        /pieplugin
            /PiePlugin.java
/view
    App.java
```

-   `Plugin.java` is an `<<interface>>` located in `/framework/plugins`
    -   `DataPlugin.java` & `VisualPlugin.java` will extends `Plugin`
        -   `TwitterPlugin.java`, `NewsPlugin.java` extends `DataPlugin.java`
            -   `init(Map<String, String> params)` is implemente in each subclass to initialize the parameterize the corresponding datasets
        -   `BarPlugin.java` & `PiePlugin.java` extends `VisualPlugin.java`
-   In `Framework.java`, using strategy pattern with the implementation of `config(Plugin dataPlugin, List<Plugin> visualPlugins)` to initialize the framework with the corresponding plugins
    -   To dynamically change the plugin at runtime and delegation work to the specific plugins

### Generality vs Specifity

The framework is to analyze sentiment of articles/trends collected within a specific period of time and happened at a specific location. For example, analyzing a set of articles/trends, whether they are positive, negative or normal, based on scores. The framework can work for wide range of difference data resources like Twitter texts (using Twitter4J API) and News headlines (using News API) provided by data plugins. Furture plugins maintainning the fields of `text` content, `time_stamp`, and perferably `location` fields would be also supported by this data visualizatio framework. Also, there are difference ways of visualizatons for displaying the dataset, e.g. pie chart and bar chart. Display sentiment scores using bar chart over timestamp and display the proportion of different sentiment scores using pie chart.

The source data abstractions should be a list of text fragments with time stamps and locations. Then the framework can go through some steps to get the sentiment scores, sends the results back to the web page and displays them. The result data should have sentiment scores and timestamps.

For the flexibility of plugins, a top interface `Plugin` was extended by `DataPlugin` and `VisualPlugin`. Extracting the top overlapped functionalities into the `Plugin` interface and area-specific methods in the data and visualization plugins, respectively. In this way, more concrete subclasses of data and visualization plugins could be added by simply `extends` the corresponding interface without changing code to other parts.

As for the framework, it receives the data from data plugins and do sentiments analysis using Google's Natural Language API. If there would be other data plugins, we can reuse the framework to analyze sentiment without change the codes, which makes the framework reusable. Same with the visualization plugins, it is easy to reuse the framework even more visualization plugins are added in the future. Therefore, without changing framework, it is flexible to add plugins for data and visualization.

### Plugin interfaces

#### DataPlugin

Interface DataPlugin has three key methods:

```java
/**
 * initialize the value of field e.g. timestamp
 */
setup(<String, String> parameterMap)

/**
 * get data using API
 */
getDataFromParams()

/**
 * parse the data from getDataFromParams() and parse it to corresponding type
 * e.g. string for text, Data for timestamp
 */
parseData()
```

TwitterDataPlugin uses Twitter4J API to get data, examples for Twitter4J lib can be found: https://github.com/Twitter4J/Twitter4J/blob/master/twitter4j-examples/src/main/java/twitter4j/examples

```java
 Twitter twitter = new TwitterFactory().getInstance();
        try {
            String cursor = null;
            int count = 20;
            DirectMessageList messages;
            do {
                System.out.println("* cursor:" + cursor);
                messages = cursor == null ? twitter.getDirectMessages(count) : twitter.getDirectMessages(count, cursor);
                for (DirectMessage message : messages) {
                    System.out.println("From: " + message.getSenderId() + " id:" + message.getId()
                            + " [" + message.getCreatedAt() + "]"
                            + " - " + message.getText());
                    System.out.println("raw[" + message + "]");
                }
                cursor = messages.getNextCursor();
            } while (messages.size() > 0 && cursor != null);
            System.out.println("done.");
            System.exit(0);
        } catch (TwitterException te) {
            te.printStackTrace();
            System.out.println("Failed to get messages: " + te.getMessage());
            System.exit(-1);
        }
```

NewsDataPlugin uses News API to get data, examples can be found here:https://github.com/KwabenBerko/News-API-Java

```java
newsApiClient.getEverything(
                new EverythingRequest.Builder()
                        .q("trump")
                        .build(),
                new NewsApiClient.ArticlesResponseCallback() {
                    @Override
                    public void onSuccess(ArticleResponse response) {
                        System.out.println(response.getArticles().get(0).getTitle());
                    }

                    @Override
                    public void onFailure(Throwable throwable) {
                        System.out.println(throwable.getMessage());
                    }
                }
        );
```

The return data from DataPlugin should be a list of (string) articles with (Date) timestamps, so the framework can do sentiment analysis and calculate scores.

#### VisualizationPlugin

Interface VisualizationPlugin has three key methods:

```java
/**
 * display the list of data provided by the framework
 */
display(Date[] timestamp, double[] scores)

```

Bar chart:
example code can be found: https://plotly.com/javascript/bar-charts/

```JavaScript
var data = [
  {
    x: ['giraffes', 'orangutans', 'monkeys'],
    y: [20, 14, 23],
    type: 'bar'
  }
];

Plotly.newPlot('myDiv', data);
```

Pie chart:
example code can be found: https://plotly.com/javascript/pie-charts/

```JavaScript
var data = [{
  values: [19, 26, 55],
  labels: ['Residential', 'Non-Residential', 'Utility'],
  type: 'pie'
}];

var layout = {
  height: 400,
  width: 500
};

Plotly.newPlot('myDiv', data, layout);

```

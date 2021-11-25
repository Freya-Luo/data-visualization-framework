# hw6-analytics-main.java.edu.cmu.cs214.hw6.framework-haveagoodday
##How to start
Before running this framework, you need to set Environment Path for google nlp API:
````shell
export GOOGLE_APPLICATION_CREDENTIALS=${PROJ_PATH}/framework-332720-6c53b39aa17c.json
````
If there is error like :
```
java.io.IOException: 
The Application Default Credentials are not available. 
They are available if running in Google Compute Engine. Otherwise, the environment variable GOOGLE_APPLICATION_CREDENTIALS must be defined pointing to a file defining the credentials.
```
Try enter `export GOOGLE_APPLICATION_CREDENTIALS=${PROJ_PATH}/framework-332720-6c53b39aa17c.json
` in IntelliJ terminal instead of modify the configuration of IntelliJ.

After successfully set GOOGLE_APPLICATION_CREDENTIALS environment path, run the framework:
<br>
````
mvn site && mvn exec:exec
````
Then you can try on http://localhost:8080/

##Our Framework
Our framework is to analyze sentiment of articles/trends collected within a specific period of time. 
For example, analyzing a set of texts, whether they are positive, negative or normal, based on scores. 
The framework can work for wide range of difference data resources like Twitter texts (using Twitter4J API) and News headlines (using News API) provided by data plugins. 
Future plugins maintaining the fields of text content, time_stamp, and perferably location fields would be also supported by this data visualizatio main.java.edu.cmu.cs214.hw6.framework. Also, there are difference ways of visualizatons for displaying the dataset, e.g. pie chart and bar chart. Display sentiment scores using bar chart over timestamp and display the proportion of different sentiment scores using pie chart.
`Data Plugins` provide a list of `text` fields with `time_stamp` and a `locate_at` field. The possible `Data Plugins` would be implemented as follows:

-   `TwitterPlugin`
    -   Fetches a list of Twiiter Trends which happen at a specific location and within a specific period of time using the Twitter API
-   `NewsPlugin`
    -   Fetches a list News Articles of a country that were published within a period of time with News API

The main.java.edu.cmu.cs214.hw6.framework sends a list of `text` along with `time_stamp` and a `locate_at` data input to the `Visualization Plugins`, which could present the following features:

-   Bar charts showing the magnitude of positive`Trends/Events` over time
-   Pie Charts displaying the relative number of postive and negative `Trends/Events` within a period of time

The main.java.edu.cmu.cs214.hw6.framework processes data from the above fields and analyze the text sentiment from the given `text` fields in `core`, providing statuses to the `gui` for rendering.

-   `Plugin.java` is an `<<interface>>` located in `/main.java.edu.cmu.cs214.hw6.framework/plugins`
    -   `DataPlugin.java` & `VisualPlugin.java` will extends `Plugin`
        -   `TwitterPlugin.java`, `NewsPlugin.java` extends `DataPlugin.java`
            -   `init(Map<String, String> params)` is implemente in each subclass to initialize the parameterize the corresponding datasets
        -   `BarPlugin.java` & `PiePlugin.java` extends `VisualPlugin.java`
-   In `Framework.java`, using strategy pattern with the implementation of `config(Plugin dataPlugin, List<Plugin> visualPlugins)` to initialize the main.java.edu.cmu.cs214.hw6.framework with the corresponding plugins
    -   To dynamically change the main.java.edu.cmu.cs214.hw6.plugin at runtime and delegation work to the specific plugins


The main.java.edu.cmu.cs214.hw6.framework is to analyze sentiment of articles/trends collected within a specific period of time and happened at a specific location. For example, analyzing a set of articles/trends, whether they are positive, negative or normal, based on scores. The main.java.edu.cmu.cs214.hw6.framework can work for wide range of difference data resources like Twitter texts (using Twitter4J API) and News headlines (using News API) provided by data plugins. Furture plugins maintainning the fields of `text` content, `time_stamp`, and perferably `location` fields would be also supported by this data visualizatio main.java.edu.cmu.cs214.hw6.framework. Also, there are difference ways of visualizatons for displaying the dataset, e.g. pie chart and bar chart. Display sentiment scores using bar chart over timestamp and display the proportion of different sentiment scores using pie chart.

The source data abstractions should be a list of text fragments with time stamps and locations. Then the main.java.edu.cmu.cs214.hw6.framework can go through some steps to get the sentiment scores, sends the results back to the web page and displays them. The result data should have sentiment scores and timestamps.

For the flexibility of plugins, a top interface `Plugin` was extended by `DataPlugin` and `VisualPlugin`. Extracting the top overlapped functionalities into the `Plugin` interface and area-specific methods in the data and visualization plugins, respectively. In this way, more concrete subclasses of data and visualization plugins could be added by simply `extends` the corresponding interface without changing code to other parts.

As for the main.java.edu.cmu.cs214.hw6.framework, it receives the data from data plugins and do sentiments analysis using Google's Natural Language API. If there would be other data plugins, we can reuse the main.java.edu.cmu.cs214.hw6.framework to analyze sentiment without change the codes, which makes the main.java.edu.cmu.cs214.hw6.framework reusable. Same with the visualization plugins, it is easy to reuse the main.java.edu.cmu.cs214.hw6.framework even more visualization plugins are added in the future. Therefore, without changing main.java.edu.cmu.cs214.hw6.framework, it is flexible to add plugins for data and visualization.

##how to extend
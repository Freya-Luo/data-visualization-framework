# hw6-analytics-framework-haveagoodday

##How to start
Before running this framework, you need to set Environment Path for google nlp API:
````shell
export GOOGLE_APPLICATION_CREDENTIALS=${PROJ_PATH}/framework-332720-6c53b39aa17c.json
````
It is better to use absolute path for PROJ_PATH, which is the absolute path of `framework-332720-6c53b39aa17c.json` file.

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
The framework can work for wide range of difference data resources and we provide sample data plugins --
Twitter texts (using Twitter4J API) and News headlines (using News API). 

`Data Plugins` can fetch data via API and return a list of Content class including `text` and `time_stamp`. Sample data plugins are:

-   `TwitterPlugin`
    -   Fetches a list of Twitter Trends which happened within a specific period of time using the Twitter4J API
-   `NewsPlugin`
    -   Fetches a list News Articles that were published within a period of time with News API

The data plugins should have text with time stamps. Then the framework can go through some steps to get the sentiment scores, sends the results back to the visualization plugins and displays them on the GUI. 
The result data will have sentiment scores and timestamps.

After analyzed by Google NLP API, the framework sends a list of Content class including `text`, `time_stamp` and `sentiment score` to the `Visualization Plugins`, 
which could present the following features. We also provide two sample visualization plugins as below:

-   Bar charts showing the magnitude of positive`Trends/Events` over time
-   Pie Charts displaying the relative number of positive and negative `Trends/Events`

##how to extend
For data plugins: as long as the data has texts and timestamp, we can analyze the sentiment using our framework. DataPlugins should provide list of Content class which contains texts, timestamps.

For Visualization plugins: our framework offers scores and timestamps for charts, therefore, any chart can use there two elements to draw different charts.

Our framework would utilize the text from data plugins, analyze the sentiment and calculate the scores. Return scores and timestamps to visualization plugins, then visualization plugins can draw different charts as they want.
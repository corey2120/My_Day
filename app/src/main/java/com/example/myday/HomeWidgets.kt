package com.example.myday

import android.content.Intent
import android.net.Uri
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.net.URL
import java.time.LocalTime
import org.json.JSONObject

data class NewsArticle(
    val title: String,
    val description: String,
    val url: String,
    val source: String
)

@Composable
fun GreetingWidget() {
    val currentTime = remember { LocalTime.now() }
    val greeting = when (currentTime.hour) {
        in 0..11 -> "Good Morning"
        in 12..16 -> "Good Afternoon"
        else -> "Good Evening"
    }
    
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 8.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.primaryContainer
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(
            modifier = Modifier.padding(20.dp)
        ) {
            Text(
                text = greeting,
                style = MaterialTheme.typography.headlineMedium,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onPrimaryContainer
            )
            Text(
                text = "Welcome back!",
                style = MaterialTheme.typography.bodyLarge,
                color = MaterialTheme.colorScheme.onPrimaryContainer.copy(alpha = 0.8f)
            )
        }
    }
}

@Composable
fun QuoteWidget() {
    val quotes = remember {
        listOf(
            "The only way to do great work is to love what you do." to "Steve Jobs",
            "Success is not final, failure is not fatal: it is the courage to continue that counts." to "Winston Churchill",
            "Believe you can and you're halfway there." to "Theodore Roosevelt",
            "The future belongs to those who believe in the beauty of their dreams." to "Eleanor Roosevelt",
            "It does not matter how slowly you go as long as you do not stop." to "Confucius",
            "Everything you've ever wanted is on the other side of fear." to "George Addair",
            "Believe in yourself. You are braver than you think, more talented than you know, and capable of more than you imagine." to "Roy T. Bennett",
            "I learned that courage was not the absence of fear, but the triumph over it." to "Nelson Mandela",
            "There is only one way to avoid criticism: do nothing, say nothing, and be nothing." to "Aristotle",
            "Do what you can with all you have, wherever you are." to "Theodore Roosevelt"
        )
    }
    
    val (quote, author) = remember { quotes.random() }
    
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 8.dp),
        border = BorderStroke(1.dp, MaterialTheme.colorScheme.outline.copy(alpha = 0.3f)),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(
            modifier = Modifier.padding(20.dp)
        ) {
            Text(
                text = "💭 Quote of the Day",
                style = MaterialTheme.typography.labelLarge,
                color = MaterialTheme.colorScheme.primary,
                modifier = Modifier.padding(bottom = 12.dp)
            )
            Text(
                text = "\"$quote\"",
                style = MaterialTheme.typography.bodyLarge,
                fontWeight = FontWeight.Medium
            )
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = "— $author",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f)
            )
        }
    }
}

@Composable
fun NewsWidget(category: String) {
    var newsArticles by remember { mutableStateOf<List<NewsArticle>>(emptyList()) }
    var isLoading by remember { mutableStateOf(true) }
    var error by remember { mutableStateOf<String?>(null) }
    var refreshTrigger by remember { mutableStateOf(0) }
    
    // Load news on initial composition and when refreshTrigger or category changes
    LaunchedEffect(category, refreshTrigger) {
        isLoading = true
        error = null
        try {
            newsArticles = fetchNews(category)
        } catch (e: Exception) {
            error = "Failed to load news"
            newsArticles = getDemoNews(category)
        } finally {
            isLoading = false
        }
    }
    
    // Auto-refresh every 30 minutes (separate effect to avoid conflict)
    LaunchedEffect(category) {
        kotlinx.coroutines.delay(30 * 60 * 1000L) // Wait 30 minutes before first auto-refresh
        while (true) {
            refreshTrigger++
            kotlinx.coroutines.delay(30 * 60 * 1000L) // 30 minutes
        }
    }
    
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 8.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surfaceVariant
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(
            modifier = Modifier.padding(20.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "📰 ${category.replaceFirstChar { it.uppercase() }} News",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                IconButton(
                    onClick = { refreshTrigger++ },
                    modifier = Modifier.size(32.dp)
                ) {
                    Icon(
                        Icons.Default.Refresh,
                        contentDescription = "Refresh News",
                        tint = MaterialTheme.colorScheme.primary
                    )
                }
            }
            
            Spacer(modifier = Modifier.height(12.dp))
            
            when {
                isLoading -> {
                    CircularProgressIndicator(
                        modifier = Modifier
                            .size(32.dp)
                            .align(Alignment.CenterHorizontally)
                    )
                }
                error != null -> {
                    Text(
                        text = error!!,
                        color = MaterialTheme.colorScheme.error,
                        style = MaterialTheme.typography.bodyMedium
                    )
                }
                newsArticles.isEmpty() -> {
                    Text(
                        text = "No news available",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.6f)
                    )
                }
                else -> {
                    Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                        newsArticles.take(5).forEach { article ->
                            NewsItem(article)
                            if (article != newsArticles.take(5).last()) {
                                HorizontalDivider(
                                    modifier = Modifier.padding(vertical = 4.dp),
                                    color = MaterialTheme.colorScheme.outline.copy(alpha = 0.2f)
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun NewsItem(article: NewsArticle) {
    val context = LocalContext.current
    
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .clickable {
                try {
                    android.util.Log.d("NewsWidget", "Attempting to open URL: ${article.url}")
                    if (article.url.isNotBlank()) {
                        val intent = Intent(Intent.ACTION_VIEW).apply {
                            data = Uri.parse(article.url)
                            addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                        }
                        // Check if there's an app that can handle this intent
                        if (intent.resolveActivity(context.packageManager) != null) {
                            context.startActivity(intent)
                            android.util.Log.d("NewsWidget", "Successfully opened URL: ${article.url}")
                        } else {
                            android.util.Log.e("NewsWidget", "No app found to handle URL: ${article.url}")
                        }
                    } else {
                        android.util.Log.e("NewsWidget", "Article URL is blank")
                    }
                } catch (e: Exception) {
                    android.util.Log.e("NewsWidget", "Error opening URL: ${article.url}", e)
                }
            }
    ) {
        Text(
            text = article.title,
            style = MaterialTheme.typography.bodyLarge,
            fontWeight = FontWeight.SemiBold,
            maxLines = 2
        )
        if (article.description.isNotBlank()) {
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = article.description,
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.8f),
                maxLines = 2
            )
        }
        Spacer(modifier = Modifier.height(4.dp))
        Text(
            text = article.source,
            style = MaterialTheme.typography.labelSmall,
            color = MaterialTheme.colorScheme.primary
        )
    }
}

private suspend fun fetchNews(category: String): List<NewsArticle> {
    return withContext(Dispatchers.IO) {
        try {
            // Using Google News RSS feeds (completely free, no API key needed!)
            val rssUrl = when(category) {
                "world" -> "https://news.google.com/rss/topics/CAAqJggKIiBDQkFTRWdvSUwyMHZNRGx1YlY4U0FtVnVHZ0pWVXlnQVAB?hl=en-US&gl=US&ceid=US:en"
                "business" -> "https://news.google.com/rss/topics/CAAqJggKIiBDQkFTRWdvSUwyMHZNRGx6TVdZU0FtVnVHZ0pWVXlnQVAB?hl=en-US&gl=US&ceid=US:en"
                "technology" -> "https://news.google.com/rss/topics/CAAqJggKIiBDQkFTRWdvSUwyMHZNRGRqTVhZU0FtVnVHZ0pWVXlnQVAB?hl=en-US&gl=US&ceid=US:en"
                "science" -> "https://news.google.com/rss/topics/CAAqJggKIiBDQkFTRWdvSUwyMHZNRFp0Y1RjU0FtVnVHZ0pWVXlnQVAB?hl=en-US&gl=US&ceid=US:en"
                "health" -> "https://news.google.com/rss/topics/CAAqIQgKIhtDQkFTRGdvSUwyMHZNR3QwTlRFU0FtVnVLQUFQAQ?hl=en-US&gl=US&ceid=US:en"
                "sports" -> "https://news.google.com/rss/topics/CAAqJggKIiBDQkFTRWdvSUwyMHZNRFp1ZEdvU0FtVnVHZ0pWVXlnQVAB?hl=en-US&gl=US&ceid=US:en"
                "entertainment" -> "https://news.google.com/rss/topics/CAAqJggKIiBDQkFTRWdvSUwyMHZNREpxYW5RU0FtVnVHZ0pWVXlnQVAB?hl=en-US&gl=US&ceid=US:en"
                else -> "https://news.google.com/rss?hl=en-US&gl=US&ceid=US:en" // Top stories
            }
            
            android.util.Log.d("NewsWidget", "Fetching Google News RSS for category: $category")
            val url = URL(rssUrl)
            val connection = url.openConnection()
            connection.setRequestProperty("User-Agent", "Mozilla/5.0")
            connection.connectTimeout = 10000
            connection.readTimeout = 10000
            
            val response = connection.getInputStream().bufferedReader().use { it.readText() }
            android.util.Log.d("NewsWidget", "RSS feed received, parsing...")
            
            val articlesList = parseGoogleNewsRSS(response)
            
            if (articlesList.isNotEmpty()) {
                android.util.Log.d("NewsWidget", "Successfully fetched ${articlesList.size} articles from Google News")
                return@withContext articlesList
            }
            
            // Fallback to demo data
            android.util.Log.d("NewsWidget", "No results from RSS, using demo data")
            getDemoNews(category)
        } catch (e: Exception) {
            android.util.Log.e("NewsWidget", "Error fetching news from Google News RSS", e)
            getDemoNews(category)
        }
    }
}

private fun parseGoogleNewsRSS(rssXml: String): List<NewsArticle> {
    val articles = mutableListOf<NewsArticle>()
    
    try {
        // Simple XML parsing for RSS feed
        val itemPattern = "<item>(.*?)</item>".toRegex(RegexOption.DOT_MATCHES_ALL)
        val titlePattern = "<title><!\\[CDATA\\[(.*?)\\]\\]></title>".toRegex()
        val linkPattern = "<link>(.*?)</link>".toRegex()
        val descPattern = "<description><!\\[CDATA\\[(.*?)\\]\\]></description>".toRegex()
        val sourcePattern = "<source.*?>(.*?)</source>".toRegex()
        
        val items = itemPattern.findAll(rssXml).take(5)
        
        for (item in items) {
            val itemText = item.groupValues[1]
            
            val title = titlePattern.find(itemText)?.groupValues?.get(1)?.trim() ?: ""
            val link = linkPattern.find(itemText)?.groupValues?.get(1)?.trim() ?: ""
            val desc = descPattern.find(itemText)?.groupValues?.get(1)?.trim() ?: ""
            val source = sourcePattern.find(itemText)?.groupValues?.get(1)?.trim() ?: "Google News"
            
            if (title.isNotBlank() && link.isNotBlank()) {
                // Clean up description (remove HTML tags if present)
                val cleanDesc = desc.replace("<.*?>".toRegex(), "").trim()
                
                android.util.Log.d("NewsWidget", "Parsed article: $title -> $link")
                articles.add(
                    NewsArticle(
                        title = title,
                        description = cleanDesc.take(150),
                        url = link,
                        source = source
                    )
                )
            }
        }
    } catch (e: Exception) {
        android.util.Log.e("NewsWidget", "Error parsing RSS XML", e)
    }
    
    return articles
}

private fun getDemoNews(category: String): List<NewsArticle> {
    return when (category) {
        "technology" -> listOf(
            NewsArticle(
                title = "Major Breakthrough in Quantum Computing",
                description = "Scientists achieve new milestone in quantum processor development.",
                url = "https://www.technologyreview.com/",
                source = "MIT Tech Review"
            ),
            NewsArticle(
                title = "AI Models Show Significant Improvements",
                description = "Latest generation of language models demonstrate enhanced capabilities.",
                url = "https://www.theverge.com/tech",
                source = "The Verge"
            ),
            NewsArticle(
                title = "Cybersecurity Threats Evolve Rapidly",
                description = "Experts warn of new sophisticated attack vectors.",
                url = "https://www.wired.com/category/security/",
                source = "Wired"
            ),
            NewsArticle(
                title = "Tech Giants Announce New Initiatives",
                description = "Major companies unveil plans for sustainable technology.",
                url = "https://techcrunch.com/",
                source = "TechCrunch"
            ),
            NewsArticle(
                title = "Innovative App Helps Users Stay Productive",
                description = "New productivity tools are changing how we manage our daily tasks.",
                url = "https://www.androidauthority.com/",
                source = "Android Authority"
            )
        )
        "business" -> listOf(
            NewsArticle(
                title = "Global Markets Show Strong Recovery",
                description = "Stock markets around the world experience positive growth.",
                url = "https://www.reuters.com/markets/",
                source = "Reuters"
            ),
            NewsArticle(
                title = "Startup Funding Reaches Record Levels",
                description = "Venture capital investment continues strong trend.",
                url = "https://www.bloomberg.com/",
                source = "Bloomberg"
            ),
            NewsArticle(
                title = "Economic Indicators Point to Growth",
                description = "Analysts optimistic about future economic performance.",
                url = "https://www.ft.com/",
                source = "Financial Times"
            ),
            NewsArticle(
                title = "Corporate Innovation Drives Change",
                description = "Businesses adapt to new market realities.",
                url = "https://www.wsj.com/",
                source = "Wall Street Journal"
            ),
            NewsArticle(
                title = "International Trade Agreements Progress",
                description = "Nations work toward mutually beneficial partnerships.",
                url = "https://www.cnbc.com/",
                source = "CNBC"
            )
        )
        "health" -> listOf(
            NewsArticle(
                title = "New Study Reveals Benefits of Daily Exercise",
                description = "Researchers find even light activity can improve overall health.",
                url = "https://www.health.com/fitness",
                source = "Health Magazine"
            ),
            NewsArticle(
                title = "Medical Breakthrough in Treatment",
                description = "Innovative approach shows promise in clinical trials.",
                url = "https://www.healthline.com/",
                source = "Healthline"
            ),
            NewsArticle(
                title = "Nutrition Guidelines Updated",
                description = "Health experts recommend balanced approach to diet.",
                url = "https://www.medicalnewstoday.com/",
                source = "Medical News Today"
            ),
            NewsArticle(
                title = "Mental Health Awareness Grows",
                description = "New resources available to support wellbeing.",
                url = "https://www.webmd.com/",
                source = "WebMD"
            ),
            NewsArticle(
                title = "Preventive Care Shows Long-term Benefits",
                description = "Studies demonstrate value of regular health screenings.",
                url = "https://www.mayoclinic.org/",
                source = "Mayo Clinic"
            )
        )
        "sports" -> listOf(
            NewsArticle(
                title = "Championship Game Delivers Excitement",
                description = "Athletes showcase exceptional skills in thrilling match.",
                url = "https://www.espn.com/",
                source = "ESPN"
            ),
            NewsArticle(
                title = "Rising Star Makes Headlines",
                description = "Young athlete's performance captures attention.",
                url = "https://www.sportingnews.com/",
                source = "Sporting News"
            ),
            NewsArticle(
                title = "Team Announces Strategic Changes",
                description = "Organization makes moves for future success.",
                url = "https://www.si.com/",
                source = "Sports Illustrated"
            ),
            NewsArticle(
                title = "Olympic Preparations Underway",
                description = "Athletes train intensively for upcoming games.",
                url = "https://olympics.com/",
                source = "Olympics"
            ),
            NewsArticle(
                title = "Record-Breaking Performance Stuns Fans",
                description = "Historic achievement marks new milestone in sport.",
                url = "https://www.cbssports.com/",
                source = "CBS Sports"
            )
        )
        "science" -> listOf(
            NewsArticle(
                title = "Space Agency Announces New Mission Plans",
                description = "Exciting developments in space exploration expected next year.",
                url = "https://www.nasa.gov/news/",
                source = "NASA"
            ),
            NewsArticle(
                title = "Climate Research Reveals Key Findings",
                description = "Scientists present important data on environmental changes.",
                url = "https://www.sciencedaily.com/",
                source = "Science Daily"
            ),
            NewsArticle(
                title = "Archaeological Discovery Sheds Light on History",
                description = "Ancient artifacts provide new insights into past civilizations.",
                url = "https://www.nature.com/",
                source = "Nature"
            ),
            NewsArticle(
                title = "Marine Biology Study Uncovers Ocean Secrets",
                description = "Researchers explore deep sea ecosystems.",
                url = "https://www.scientificamerican.com/",
                source = "Scientific American"
            ),
            NewsArticle(
                title = "Physics Experiment Yields Surprising Results",
                description = "Latest research challenges existing theories.",
                url = "https://www.space.com/",
                source = "Space.com"
            )
        )
        else -> listOf(
            NewsArticle(
                title = "Global Leaders Meet for Summit",
                description = "International cooperation on key issues discussed.",
                url = "https://www.bbc.com/news",
                source = "BBC News"
            ),
            NewsArticle(
                title = "Community Initiative Makes Impact",
                description = "Local program demonstrates positive change.",
                url = "https://www.npr.org/",
                source = "NPR"
            ),
            NewsArticle(
                title = "Cultural Event Celebrates Diversity",
                description = "Festival brings together people from all backgrounds.",
                url = "https://www.theguardian.com/",
                source = "The Guardian"
            ),
            NewsArticle(
                title = "Education Reforms Show Promise",
                description = "New approaches to learning yield positive outcomes.",
                url = "https://www.cnn.com/",
                source = "CNN"
            ),
            NewsArticle(
                title = "Environmental Conservation Efforts Expand",
                description = "Organizations work to protect natural habitats.",
                url = "https://www.reuters.com/",
                source = "Reuters"
            )
        )
    }
}

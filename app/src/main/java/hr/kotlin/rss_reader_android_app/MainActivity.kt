package hr.kotlin.rss_reader_android_app


import android.content.Intent
import android.os.Bundle
import android.widget.ArrayAdapter
import android.widget.ListView
import androidx.appcompat.app.AppCompatActivity
import com.loopj.android.http.AsyncHttpClient
import com.loopj.android.http.AsyncHttpResponseHandler
import cz.msebera.android.httpclient.Header
import org.jsoup.Jsoup
import org.jsoup.nodes.Document
import android.os.Handler
import android.os.Looper


class MainActivity : AppCompatActivity() {
    private lateinit var listView: ListView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        listView = findViewById(R.id.listView)

        val rssLinkovi = listOf(
            "https://www.index.hr/rss/vijesti",
//            "https://www.vecernji.hr/rss/vijesti",
//            "https://www.jutarnji.hr/rss/vijesti",
//            "https://www.24sata.hr/feeds/aktualno.xml",
//            "https://www.tportal.hr/vijesti/rss",
//            "https://www.telegram.hr/feed/",
//            "https://www.nacional.hr/feed/",
//            "https://www.novilist.hr/feed/",
//            "https://www.glasistre.hr/rss/vijesti",
//            "https://www.glas-slavonije.hr/rss/vijesti",
        )
        val kljucneRijeci = listOf("vojska", "sigurnost", "danas", "sport", "pobjeda", "A3", "autocesti", "poplava", "helikopter", "na")

        val client = AsyncHttpClient(true, 80, 443)
        val handler = Handler(Looper.getMainLooper())

        val preuzmiVijestiTask = Runnable {
            val sveVijesti = ArrayList<String>()

            for (rssLink in rssLinkovi) {
                client.get(this@MainActivity, rssLink, null as Array<Header>?, null, object : AsyncHttpResponseHandler(Looper.getMainLooper()) {
                    override fun onSuccess(statusCode: Int, headers: Array<out Header>?, responseBody: ByteArray?) {
                        val responseString = responseBody?.toString(Charsets.UTF_8)
                        responseString?.let {
                            val doc: Document = Jsoup.parse(it)
                            val items = doc.select("item")
                            for (item in items) {
                                val naslov = item.selectFirst("title")?.text()
                                val sadrzaj = item.selectFirst("description")?.text()
                                val clanak = item.selectFirst("article")?.text()
                                if (naslov != null && sadrzaj != null && kljucneRijeci.any { it in sadrzaj.toLowerCase() }) {
                                    val vijest = "Naslov: $naslov\nSadržaj: $sadrzaj"

                                    sveVijesti.add(vijest)
                                }
                            }
                        }
                        handler.post {
                            onPostExecute(sveVijesti)
                        }
                    }

                    override fun onFailure(statusCode: Int, headers: Array<out Header>?, responseBody: ByteArray?, error: Throwable?) {
                        // Obrada greške ako je potrebno
                    }
                })
            }
        }

        Thread(preuzmiVijestiTask).start()
    }

    private fun onPostExecute(result: List<String>) {
        val adapter = ArrayAdapter(this, android.R.layout.simple_list_item_1, result)
        listView.adapter = adapter
        listView.setOnItemClickListener { parent, view, position, id ->
            val selectedArticle = result[position]

            val intent = Intent(this@MainActivity, ArticleActivity::class.java)
            intent.putExtra("article", selectedArticle)
            startActivity(intent)
        }
    }
}

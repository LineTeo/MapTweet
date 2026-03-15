package tweet.dao; // ← 変更

import java.io.File;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

import tweet.model.Tweet; // ← 変更

public class XmlTweetDao implements TweetDao {

    private final String filePath;

    public XmlTweetDao(String filePath) {
        this.filePath = filePath;
    }

    @Override
    public void save(Tweet tweet) {
        try {
            Document doc = loadOrCreate();
            Element root = doc.getDocumentElement();

            Element el = doc.createElement("tweet");

            Element text = doc.createElement("text");
            text.setTextContent(tweet.getText());
            el.appendChild(text);

            Element lat = doc.createElement("lat");
            lat.setTextContent(String.valueOf(tweet.getLatitude()));
            el.appendChild(lat);

            Element lng = doc.createElement("lng");
            lng.setTextContent(String.valueOf(tweet.getLongitude()));
            el.appendChild(lng);

            Element postedAt = doc.createElement("postedAt");
            postedAt.setTextContent(tweet.getPostedAt().toString());
            el.appendChild(postedAt);

		        // ★ userId を追加
		        Element userId = doc.createElement("userId");
		        userId.setTextContent(tweet.getUserId() != null ? tweet.getUserId() : "");
		        el.appendChild(userId);

            root.appendChild(el);
            writeXml(doc);

        } catch (Exception e) {
            throw new RuntimeException("XML保存失敗", e);
        }
    }

    @Override
    public List<Tweet> findAll() {
        List<Tweet> list = new ArrayList<>();
        try {
            File f = new File(filePath);
            if (!f.exists()) return list;

            Document doc = DocumentBuilderFactory.newInstance()
                    .newDocumentBuilder().parse(f);
            NodeList nodes = doc.getElementsByTagName("tweet");

            for (int i = 0; i < nodes.getLength(); i++) {
                Element el = (Element) nodes.item(i);
                Tweet t = new Tweet();
                t.setText(el.getElementsByTagName("text").item(0).getTextContent());
                t.setLatitude(Double.parseDouble(
                        el.getElementsByTagName("lat").item(0).getTextContent()));
                t.setLongitude(Double.parseDouble(
                        el.getElementsByTagName("lng").item(0).getTextContent()));
                t.setPostedAt(LocalDateTime.parse(
                        el.getElementsByTagName("postedAt").item(0).getTextContent()));
		            
		            // ★ userId を読み込む（古いデータには要素がない場合も考慮）
		            NodeList userIdNodes = el.getElementsByTagName("userId");
		            if (userIdNodes.getLength() > 0) {
		                t.setUserId(userIdNodes.item(0).getTextContent());
		            }
		            
		            list.add(t);
		        }
        } catch (Exception e) {
            throw new RuntimeException("XML読み込み失敗", e);
        }
        list.sort((a, b) -> b.getPostedAt().compareTo(a.getPostedAt()));
        return list;
    }

    private Document loadOrCreate() throws Exception {
        File f = new File(filePath);
        if (f.exists()) {
            return DocumentBuilderFactory.newInstance()
                    .newDocumentBuilder().parse(f);
        }
        Document doc = DocumentBuilderFactory.newInstance()
                .newDocumentBuilder().newDocument();
        doc.appendChild(doc.createElement("tweets"));
        return doc;
    }

    private void writeXml(Document doc) throws Exception {
        Transformer t = TransformerFactory.newInstance().newTransformer();
        t.setOutputProperty(OutputKeys.INDENT, "yes");
        t.transform(new DOMSource(doc), new StreamResult(new File(filePath)));
    }
}
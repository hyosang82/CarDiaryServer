package kr.hyosang.cardiary;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.List;
import java.util.StringTokenizer;
import java.util.logging.Logger;

import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.google.appengine.api.datastore.DatastoreService;
import com.google.appengine.api.datastore.DatastoreServiceFactory;
import com.google.appengine.api.datastore.Entity;
import com.google.appengine.api.datastore.Key;
import com.google.appengine.api.datastore.KeyFactory;
import com.google.appengine.api.datastore.PreparedQuery;
import com.google.appengine.api.datastore.Query;
import com.google.appengine.api.datastore.Query.SortDirection;
import com.google.appengine.api.datastore.Text;

import kr.hyosang.cardiary.data.model.DriveLog;
import kr.hyosang.cardiary.data.model.DriveLogData;
import kr.hyosang.cardiary.data.model.DriveLogItemSet;
import kr.hyosang.cardiary.util.Util;

@SuppressWarnings("serial")
public class Car_diaryServlet extends HttpServlet {
	public void doGet(HttpServletRequest req, HttpServletResponse resp) throws IOException {
		resp.setCharacterEncoding("UTF-8");
		
		DatastoreService ds = DatastoreServiceFactory.getDatastoreService();
		PrintWriter w = resp.getWriter();
		Query q;
		PreparedQuery pq;
		Logger logger = Logger.getLogger("Batch");
		
		String key = (String) req.getParameter("key");
		
		if(!Util.isEmpty(key)) {
			Key keyObj = KeyFactory.stringToKey(key);
			q = new Query("DriveLogItem", keyObj);
			q.addSort("timestamp", SortDirection.ASCENDING);
			pq = ds.prepare(q);
			
			double alt, lat, lng, spd;
			long ts;
			StringBuffer sb = new StringBuffer();
			List<Key> deleteKeys = new ArrayList<Key>();
			
			for(Entity e : pq.asIterable()) {
				ts = (long) e.getProperty("timestamp");
				alt = (double) e.getProperty("altitude");
				lat = (double) e.getProperty("latitude");
				lng = (double) e.getProperty("longitude");
				spd = (double) e.getProperty("speed");
				
				String item = String.format("%f|%f|%f|%f|%d$", lat, lng, alt, spd, ts);
				sb.append(item);
				
				deleteKeys.add(e.getKey());
			}
			
			DriveLogData data = new DriveLogData();
			data.mLogdata = sb.toString();
			
			ds.put(data.asNewEntity(keyObj));
			
			ds.delete(deleteKeys);
		}

		/* 데이터 뽑아오기
		{
			Key vk = KeyFactory.stringToKey("agljYXItZGlhcnlyKwsSBk15VXNlchiAgICAgKiiCwwLEgtWZWhpY2xlSW5mbxiAgICAgKjiCAw");
			Key logK = KeyFactory.stringToKey("agtzfmNhci1kaWFyeXIzCxIHVmVoaWNsZSIRS05NQzRDMkhNOFAwOTM1MTAMCxIIVHJhY2tMb2cYgICAgOCwxQgM");
			
			q = new Query("TrackLog", logK);
			pq = ds.prepare(q);
			Entity e = pq.asSingleEntity();
			
			w.write("" + (double) e.getProperty("distance"));
			w.write("" + (long)e.getProperty("timestamp"));
			
			q = new Query("TrackLogData", logK);
			pq = ds.prepare(q);
			for(Entity ee : pq.asIterable()) {
				Text t = (Text) ee.getProperty("logdata");
				w.println("----------------------");
				w.println(t.getValue());
			}
		}
		*/
		
		
		/* 저장
		
		{
			String logdata = "36.344707|126.602459|80.5|28.4605|1404646505254$36.345083|126.602306|73.7|22.5|1404646512157$36.345519|126.602085|66.5|27.9|1404646518261$36.345820|126.601739|54.6|27|1404646524186$36.345579|126.601306|45|36|1404646530177$36.345367|126.600635|42.3|40.5|1404646536184$36.345131|126.599752|41.3|44.1|1404646543203$36.344867|126.598824|41.2|46.8|1404646550216$36.344579|126.597828|40.1|50.4|1404646557206$36.344306|126.596919|38.4|53.1|1404646563169$36.344074|126.596135|37.8|54|1404646568206$36.343766|126.595173|35.3|55.8|1404646574220$36.343457|126.594189|34.9|56.7|1404646580276$36.343153|126.593203|34.8|56.7|1404646586219$36.342827|126.592207|33.4|57.6|1404646592159$36.342408|126.591259|33.7|57.6|1404646598177$36.341965|126.590325|34.6|57.6|1404646604218$36.341548|126.589421|35.8|54.9|1404646610179$36.341196|126.588678|36.2|55.8|1404646615237$36.340810|126.587865|35.5|45.9|1404646621160$36.340567|126.587365|35|22.5|1404646627206$36.340439|126.587172|37.2|11.7|1404646684212$36.340218|126.586597|36.4|36.9|1404646690328$36.339886|126.585915|35|45|1404646696305$36.339478|126.585080|34.6|44.1|1404646703370$36.339031|126.584308|34.1|44.1|1404646710246$36.338784|126.583535|33.6|46.8|1404646716308$36.338625|126.582691|33.8|47.7|1404646722357$36.338430|126.581677|35.5|48.6|1404646729285$36.338260|126.580820|36.4|47.7|1404646735273$36.338014|126.579811|37.3|49.5|1404646742205$36.337771|126.578920|36.8|50.4|1404646748206$36.337547|126.578030|35.6|49.5|1404646754206$36.337447|126.577289|34.9|49.5|1404646759294$36.337383|126.576396|34.8|47.7|1404646765294$36.337256|126.575334|36.6|48.6|1404646772289$36.337102|126.574293|39.5|49.5|1404646779189$36.337185|126.573399|42.4|48.6|1404646785276$36.337352|126.572558|46|45|1404646791283$36.337267|126.571790|49.1|42.3|1404646797274$36.336708|126.571022|47.1|48.6|1404646804297$36.336060|126.570445|46.1|54.9|1404646810272$36.335250|126.569708|44.2|58.5|1404646817260$36.334555|126.569073|43|55.8|1404646823205$36.334134|126.568428|42.1|54|1404646828320$36.333988|126.567454|40.2|54.9|1404646834274$36.333844|126.566268|38.1|55.8|1404646841238$36.333670|126.565108|38.1|53.1|1404646848218$36.333406|126.564214|37.9|53.1|1404646854251$36.333014|126.563413|37.1|48.6|1404646860382$36.333104|126.562627|37.4|45|1404646866262$36.333686|126.562639|39.6|40.5|1404646872320$36.333961|126.563272|43.8|41.4|1404646878321$36.333623|126.564105|46.1|46.8|1404646885299$36.332690|126.564879|43.2|63|1404646893214$36.331972|126.565481|40.3|72|1404646898329$36.331271|126.566516|42.2|72.9|1404646904324$36.330936|126.567769|45.9|68.4|1404646910308$36.331033|126.569160|50.1|63.9|1404646917266$36.331317|126.570084|52|64.8|1404646922331$36.331684|126.571197|53.3|63.9|1404646928352$36.332046|126.572336|53.9|66.6|1404646934311$36.332437|126.573395|55.2|58.5|1404646940326$36.332781|126.574308|56.6|53.1|1404646946285$36.333102|126.575166|59.1|50.4|1404646952238$36.333562|126.575897|62.9|47.7|1404646958228$36.334142|126.576051|63.9|48.6|1404646963329$36.334685|126.575384|66.3|45.9|1404646970281$36.334681|126.574678|67.8|46.8|1404646975335$36.334829|126.573570|64.6|55.8|1404646982288$36.335302|126.572628|62.7|63|1404646988307$36.335994|126.571693|61.3|71.1|1404646994220$36.336758|126.570664|59.8|77.4|1404647000217$36.337446|126.569756|59|81.9|1404647005278$36.338484|126.568420|57.7|88.2|1404647012314$36.339459|126.567260|56|91.8|1404647018294$36.340728|126.566001|53.8|92.7|1404647025264$36.341884|126.565027|52.8|94.5|1404647031300$36.343353|126.563985|50.8|98.1|1404647038297$36.344717|126.563170|48.6|100.8|1404647044302$36.346136|126.562492|46.7|102.6|1404647050290$36.347860|126.561875|43.7|103.5|1404647057268$36.349378|126.561493|41.3|104.4|1404647063317$36.350911|126.561196|40.1|102.6|1404647069320$36.352690|126.560867|38.2|102.6|1404647076319$36.354212|126.560582|35.7|102.6|1404647082293$36.355480|126.560348|34.3|102.6|1404647087363$36.357255|126.560020|32.2|101.7|1404647094307$36.359001|126.559698|30.3|99.9|1404647101303$36.360722|126.559379|31.8|99|1404647108388$36.362176|126.559109|34.3|98.1|1404647114308$36.363888|126.558796|35.6|99|1404647121302$36.365598|126.558481|37.9|98.1|1404647128266$36.367050|126.558213|39.2|97.2|1404647134325$36.368483|126.557952|40.7|96.3|1404647140365$36.369911|126.557689|42.3|96.3|1404647146290$36.371125|126.557464|43.6|99|1404647151366$36.372619|126.557198|42.2|100.8|1404647157317$36.374145|126.556919|39|102.6|1404647163307$36.375953|126.556585|34.1|104.4|1404647170319$36.377735|126.556249|33.2|101.7|1404647177239$36.379466|126.555953|34.3|98.1|1404647184247$36.380931|126.555816|35.7|98.1|1404647190242$36.382147|126.555751|37.2|98.1|1404647195310$36.383600|126.555651|38.9|96.3|1404647201351$36.385048|126.555538|41.2|96.3|1404647207293$36.386718|126.555426|43.2|95.4|1404647214317$36.388148|126.555326|44.8|95.4|1404647220324$36.389573|126.555217|47.1|95.4|1404647226353$36.390983|126.555121|49.8|92.7|1404647232448$36.392360|126.555010|51.4|91.8|1404647238236$36.393738|126.554892|54.4|91.8|1404647244236$36.394883|126.554816|56.6|93.6|1404647249316$36.396527|126.554805|58.9|94.5|1404647256308$36.397945|126.554942|60.9|94.5|1404647262396$36.399334|126.555202|64.3|91.8|1404647268324$36.400685|126.555588|66.3|91.8|1404647274340$36.402017|126.556107|67.3|92.7|1404647280305$36.403513|126.556879|68.3|93.6|1404647287282$36.404767|126.557684|66.6|95.4|1404647293314$36.406015|126.558632|65.5|98.1|1404647299344$36.407221|126.559712|64.5|99|1404647305469$36.408392|126.560827|61.9|98.1|1404647311315$36.409742|126.562126|59.2|97.2|1404647318345$36.410893|126.563233|58|96.3|1404647324309$36.412048|126.564358|57.5|99|1404647330317$36.413249|126.565521|56.4|102.6|1404647336386$36.414682|126.566909|56.2|103.5|1404647343286$36.415696|126.567889|57.2|102.6|1404647348324$36.417099|126.569243|58.2|100.8|1404647355306$36.418275|126.570400|57.9|99.9|1404647361331$36.419458|126.571549|57.3|101.7|1404647367325$36.420894|126.572892|55.8|103.5|1404647374251$36.421962|126.573781|54.3|102.6|1404647379329$36.423294|126.574767|52.4|104.4|1404647385293$36.424452|126.575536|53.9|104.4|1404647390355$36.426066|126.576441|54.9|99|1404647397331$36.427428|126.577092|55.8|96.3|1404647403331$36.428562|126.577554|56.5|93.6|1404647408393$36.429920|126.578058|57.2|94.5|1404647414339$36.431290|126.578569|57.8|95.4|1404647420337$36.432665|126.579084|58.4|94.5|1404647426298$36.434014|126.579584|58.9|92.7|1404647432354$36.435348|126.580080|58.5|92.7|1404647438359$36.436911|126.580670|56.2|94.5|1404647445314$36.438517|126.581308|51.8|97.2|1404647452300$36.439918|126.581915|49.7|99.9|1404647458306$36.441338|126.582561|46.2|101.7|1404647464327$36.442787|126.583250|44|104.4|1404647470354$36.444476|126.584047|39.2|103.5|1404647477338$36.446159|126.584850|35.4|102.6|1404647484336$36.447575|126.585519|35.8|99.9|1404647490298$36.448740|126.586066|37.8|99|1404647495372$36.450132|126.586724|40.6|99|1404647501342$36.451529|126.587386|42.5|99|1404647507346$36.452885|126.588046|45.2|95.4|1404647513362$36.454192|126.588670|47.4|92.7|1404647519389$36.455509|126.589250|49.2|93.6|1404647525260$36.456856|126.589880|48.5|97.2|1404647531289$36.458244|126.590489|46.5|99|1404647537335$36.459670|126.590959|43.8|98.1|1404647543305$36.460871|126.591243|42.1|99|1404647548373$36.462306|126.591421|41.3|97.2|1404647554330$36.463758|126.591513|39.7|97.2|1404647560317$36.464962|126.591438|38.5|96.3|1404647565368$36.466401|126.591221|35.9|96.3|1404647571339$36.468072|126.590802|35.5|96.3|1404647578293$36.469470|126.590355|35.2|94.5|1404647584370$36.471080|126.589828|35.3|95.4|1404647591313$36.472466|126.589379|35.4|95.4|1404647597355$36.473864|126.588913|36.5|97.2|1404647603383$36.475262|126.588460|37.3|96.3|1404647609355$36.476676|126.587996|39.3|98.1|1404647615375$36.478119|126.587529|42.1|99|1404647621362$36.479810|126.586983|44.5|99.9|1404647628271$36.481253|126.586517|46.7|99|1404647634318$36.482665|126.586051|49.5|97.2|1404647640376$36.484297|126.585522|51.8|96.3|1404647647321$36.485933|126.585036|52.9|97.2|1404647654306$36.487129|126.584774|53.1|97.2|1404647659337$36.488581|126.584540|52.6|98.1|1404647665362$36.490306|126.584384|54|99|1404647672341$36.491808|126.584354|54.4|100.8|1404647678420$36.493580|126.584472|54.1|101.7|1404647685377$36.495088|126.584681|54.7|100.8|1404647691344$36.496811|126.584975|55.7|98.1|1404647698347$36.498254|126.585223|58.2|96.3|1404647704358$36.499668|126.585453|60.3|94.5|1404647710322$36.501294|126.585740|62.9|93.6|1404647717302$36.502686|126.585974|65.9|92.7|1404647723353$36.504088|126.586211|70|93.6|1404647729334$36.505252|126.586415|71.7|94.5|1404647734412$36.506675|126.586654|73.1|96.3|1404647740348$36.508353|126.586880|73.8|96.3|1404647747369$36.510049|126.587036|73.7|97.2|1404647754305$36.511503|126.587112|72.7|98.1|1404647760403$36.512992|126.587110|70.5|99|1404647766407$36.514490|126.587060|68|99.9|1404647772347$36.515996|126.586941|65.7|100.8|1404647778316$36.517523|126.586772|64.6|102.6|1404647784396$36.519080|126.586538|63.6|104.4|1404647790359$36.520645|126.586217|63|106.2|1404647796369$36.522472|126.585757|62.1|107.1|1404647803291$36.523766|126.585378|62.5|106.2|1404647808349$36.525572|126.584817|62.6|105.3|1404647815313$36.527352|126.584267|64.1|103.5|1404647822377$36.528854|126.583808|66.3|101.7|1404647828334$36.530327|126.583358|68.6|99.9|1404647834303$36.531788|126.582922|69.2|99.9|1404647840312$36.533019|126.582564|68.7|102.6|1404647845375$36.534783|126.582242|67.2|103.5|1404647852347$36.536651|126.582036|64.7|108.9|1404647859340$36.538307|126.581944|61.4|112.5|1404647865361$36.540279|126.581791|59.2|112.5|1404647872321$36.541687|126.581684|55.5|112.5|1404647877408$36.543350|126.581572|50.7|109.8|1404647883356$36.545176|126.581413|48|102.6|1404647890342$36.546679|126.581309|49.8|99.9|1404647896255$36.547919|126.581215|51.1|98.1|1404647901364$36.549390|126.581110|51.8|97.2|1404647907338$36.550826|126.580981|54.6|95.4|1404647913365$36.552496|126.580855|60.4|95.4|1404647920312$36.553926|126.580720|63.1|96.3|1404647926320$36.555621|126.580427|67|99|1404647933362$36.557094|126.580076|68.8|101.7|1404647939344$36.558591|126.579612|69.6|104.4|1404647945334$36.559861|126.579120|68|106.2|1404647950451$36.561372|126.578414|65.7|108.9|1404647956262$36.562856|126.577573|66.4|108.9|1404647962263$36.564089|126.576822|65.2|108|1404647967350$36.565563|126.575909|66|108.9|1404647973326$36.567014|126.575009|68.9|107.1|1404647979305$36.568682|126.573969|74.3|107.1|1404647986312$36.569881|126.573220|78|108|1404647991407$36.571592|126.572154|81.7|108.9|1404647998267$36.573065|126.571237|83.3|110.7|1404648004263$36.574550|126.570330|83.2|110.7|1404648010281$36.575802|126.569563|82|111.6|1404648015395$36.577290|126.568640|80.4|109.8|1404648021332$36.578998|126.567565|76.6|109.8|1404648028349$36.580465|126.566648|74|108.9|1404648034323$36.581674|126.565898|71.7|108|1404648039400$36.583098|126.564981|69.8|108|1404648045370$36.584793|126.563933|65.2|108|1404648052358$36.586494|126.562890|61.6|107.1|1404648059342$36.587958|126.562043|59.1|107.1|1404648065282$36.589440|126.561308|56.6|106.2|1404648071298$36.590960|126.560661|54.7|107.1|1404648077318$36.592509|126.560109|53.3|108|1404648083419$36.594335|126.559507|51.6|107.1|1404648090315$36.595870|126.558998|52.8|105.3|1404648096353$36.597618|126.558407|54.2|103.5|1404648103325$36.598854|126.557989|55|102.6|1404648108420$36.600330|126.557490|55.2|101.7|1404648114377$36.601797|126.556996|56.9|101.7|1404648120312$36.603022|126.556583|58.4|100.8|1404648125355$36.604488|126.556135|61.3|100.8|1404648131374$36.605979|126.555733|64.2|102.6|1404648137380$36.607493|126.555368|67.3|103.5|1404648143423$36.609036|126.555092|69.1|104.4|1404648149396$36.610845|126.554832|72.4|103.5|1404648156368$36.612371|126.554618|75.4|101.7|1404648162271$36.613621|126.554445|77.2|99.9|1404648167331$36.615108|126.554243|79.9|99.9|1404648173415$36.616842|126.554002|83.8|99.9|1404648180366$36.618346|126.553801|86.4|100.8|1404648186322$36.619806|126.553602|89.3|96.3|1404648192390$36.621228|126.553395|90.9|96.3|1404648198394$36.622674|126.553194|92.3|96.3|1404648204392$36.624073|126.552964|94.6|96.3|1404648210355$36.625534|126.552760|96.6|99|1404648216363$36.627038|126.552580|98.1|101.7|1404648222372$36.628568|126.552494|100.7|102.6|1404648228386$36.629848|126.552478|104|101.7|1404648233420$36.631383|126.552548|108.5|101.7|1404648239332$36.632920|126.552713|111.8|102.6|1404648245292$36.634434|126.552934|114.7|101.7|1404648251284$36.635676|126.553181|117.3|101.7|1404648256393$36.637178|126.553540|118.5|102.6|1404648262440$36.638707|126.554000|119.3|105.3|1404648268385$36.640267|126.554557|117.3|108|1404648274385$36.641854|126.555136|115|109.8|1404648280287$36.643168|126.555617|113.2|108|1404648285416$36.644741|126.556210|110.4|108.9|1404648291361$36.646323|126.556785|107.1|108.9|1404648297397$";
			String vehicleKey = "agljYXItZGlhcnlyKwsSBk15VXNlchiAgICAgKiiCwwLEgtWZWhpY2xlSW5mbxiAgICAgKjiCAw";
			StringTokenizer st = new StringTokenizer(logdata, "$");
			
			double lng, lat, spd, alt;
			long ts;
			long ts_key = 0;
			
			ts = 0;
			lng = lat = spd = alt = 0.0f;
			
			//First
			while(st.hasMoreTokens()) {
				String itm = st.nextToken();
				String [] arr = itm.split("\\|");
				
				lat = Util.parseDouble(arr[0], 0);
				lng = Util.parseDouble(arr[1], 0);
				alt = Util.parseDouble(arr[2], 0);
				spd = Util.parseDouble(arr[3], 0);
				ts = Util.parseLong(arr[4], 0);
				
				if((lat == 0) || (lng == 0) || (ts == 0)) {
					continue;
				}
				
				ts_key = ts;
				break;
			}
			
			if(ts_key == 0) {
				logger.log(Level.SEVERE, "No timestamp");
				return;
			}
				
			
			//insert main
			Key vehicle = KeyFactory.stringToKey(vehicleKey);
			Vehicle v = Vehicle.getByKey(vehicle);
			if(v != null) {
				Key logParentKey;
				DriveLog driveLog = DriveLog.query(vehicle, ts_key);
				if(driveLog == null) {
					//not exists. insert one.
					driveLog = new DriveLog(ts_key);
					Entity e = driveLog.asNewEntity(vehicle);
					ds.put(e);

					logParentKey = e.getKey();
					driveLog = new DriveLog(e);
				}else {
					logParentKey = driveLog.getKey();
				}
				
				//insert sub items
				
				//insert first
				DriveLogItem item = DriveLogItem.query(logParentKey, ts);
				if(item == null) {
					item = new DriveLogItem(ts, lng, lat, alt, spd);
					ds.put(item.asNewEntity(logParentKey));
				}
				
				//loop
				while (st.hasMoreTokens()) {
					String itm = st.nextToken();
					String[] arr = itm.split("\\|");

					lat = Util.parseDouble(arr[0], 0);
					lng = Util.parseDouble(arr[1], 0);
					alt = Util.parseDouble(arr[2], 0);
					spd = Util.parseDouble(arr[3], 0);
					ts = Util.parseLong(arr[4], 0);

					if ((lat == 0) || (lng == 0) || (ts == 0)) {
						continue;
					}
					
					item = DriveLogItem.query(logParentKey, ts);
					if(item == null) {
						item = new DriveLogItem(ts, lng, lat, alt, spd);
						ds.put(item.asNewEntity(logParentKey));
					}
				}
				
				//dep, dest check
				w.println("KEY : " + KeyFactory.keyToString(driveLog.getKey()));
				w.println("DEP : " + driveLog.mDeparture);
				w.println("DEST : " + driveLog.mDestination);
				driveLog.updateDepDest();
			}
		}
		*/
	}
	
	private List<String> getGpsData(Key key, List<Key> keySet) {
		DatastoreService ds = DatastoreServiceFactory.getDatastoreService();
		Query q = new Query("TrackLogData", key);
		q.addSort("timestamp", SortDirection.ASCENDING);
		
		List<String> rtn = new ArrayList<String>();
		
		PreparedQuery pq = ds.prepare(q);
		
		for(Entity e : pq.asIterable()) {
			String logdata = ((Text)e.getProperty("logdata")).getValue();
			
			rtn.add(logdata);
			
			keySet.add(e.getKey());
		}
		
		return rtn;
	}
}

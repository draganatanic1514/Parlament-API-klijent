package parlament.api_komunikacija;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.LinkedList;
import java.util.List;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;

import parlament.poslanik.Poslanik;

public class ParlamentAPIKomunikacija {

	private static final String membersURL = "http://147.91.128.71:9090/parlament/api/members";
	private static final SimpleDateFormat format = new SimpleDateFormat("dd.MM.yyyy.");

	public static List<Poslanik> vratiPoslanike() throws Exception {
		List<Poslanik> poslanici = new LinkedList<>();

		String result = sendGet(membersURL);

		Gson gson = new GsonBuilder().create();

		JsonArray poslaniciJson = gson.fromJson(result, JsonArray.class);

		for (int i = 0; i < poslaniciJson.size(); i++) {
			JsonObject poslanikJson = (JsonObject) poslaniciJson.get(i);

			Poslanik p = new Poslanik();
			p.setId(poslanikJson.get("id").getAsInt());
			p.setIme(poslanikJson.get("name").getAsString());
			p.setPrezime(poslanikJson.get("lastName").getAsString());

			if (poslanikJson.get("birthDate") != null) {
				Date datum = format.parse(poslanikJson.get("birthDate").getAsString());
				p.setDatumRodjenja(datum);
			}

			poslanici.add(p);
		}

		return poslanici;
	}

	private static String sendGet(String stringUrl) throws Exception {
		URL url = new URL(stringUrl);
		HttpURLConnection con = (HttpURLConnection) url.openConnection();

		con.setRequestMethod("GET");

		BufferedReader in = new BufferedReader(new InputStreamReader(con.getInputStream()));

		boolean kraj = false;
		String tekst = "";
		while (!kraj) {
			String line = in.readLine();

			if (line == null) {
				kraj = true;
			} else {
				tekst += line;
			}
		}
		in.close();
		return tekst;
	}
}

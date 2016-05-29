package gui.kontroler;

import java.awt.EventQueue;
import java.io.BufferedWriter;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.PrintWriter;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.LinkedList;
import java.util.List;

import javax.swing.JOptionPane;

import com.google.gson.GsonBuilder;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;

import gui.GlavniProzor;
import gui.poslanik_table_model.PoslanikTableModel;
import parlament.api_komunikacija.ParlamentAPIKomunikacija;
import parlament.poslanik.Poslanik;

public class GUIKontroler {

	private static GlavniProzor glavniProzor;
	private static final String lokacija = "data/serviceMembers.json";

	public static void main(String[] args) {
		EventQueue.invokeLater(new Runnable() {

			@Override
			public void run() {
				try {
					glavniProzor = new GlavniProzor();
					glavniProzor.setLocationRelativeTo(null);
					glavniProzor.setVisible(true);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		});
	}

	public static void ispis(String tekst) {
		glavniProzor.getTextArea().append(tekst + System.lineSeparator());
	}

	public static void vratiPoslanikeJSON() {

		try {
			JsonArray poslaniciJson = ParlamentAPIKomunikacija.vratiPoslanikeUJsonFormatu();

			PrintWriter out = new PrintWriter(new BufferedWriter(new FileWriter(lokacija)));

			String tekst = new GsonBuilder().setPrettyPrinting().create().toJson(poslaniciJson);
			out.println(tekst);

			out.close();

			ispis("Poslanici su preuzeti sa servisa.");
		} catch (Exception e) {
			JOptionPane.showMessageDialog(glavniProzor, "Doslo je do greske!", "Greska", JOptionPane.ERROR_MESSAGE);
		}
	}

	public static List<Poslanik> deserijalizacija() throws Exception {
		List<Poslanik> poslanici = new LinkedList<>();

		FileReader in = new FileReader(lokacija);

		JsonArray poslaniciJson = new GsonBuilder().create().fromJson(in, JsonArray.class);

		in.close();

		for (int i = 0; i < poslaniciJson.size(); i++) {
			JsonObject jsonObject = (JsonObject) poslaniciJson.get(i);

			Poslanik p = new Poslanik();
			p.setId(jsonObject.get("id").getAsInt());
			p.setIme(jsonObject.get("name").getAsString());
			p.setPrezime(jsonObject.get("lastName").getAsString());
			if (jsonObject.get("birthDate") != null) {
				try {
					p.setDatumRodjenja((Date) new SimpleDateFormat("dd.MM.yyyy.")
							.parse(jsonObject.get("birthDate").getAsString()));
				} catch (ParseException e) {
				}
			}

			poslanici.add(p);
		}

		return poslanici;
	}

	public static void popuni() {
		try {
			List<Poslanik> poslanici = deserijalizacija();

			PoslanikTableModel p = (PoslanikTableModel) glavniProzor.getTable().getModel();
			p.setPoslanici(poslanici);

			ispis("Tabela je popunjena podacima preuzetim sa servisa");
		} catch (Exception e) {
			JOptionPane.showMessageDialog(glavniProzor, "Doslo je do greske", "Greska", JOptionPane.ERROR_MESSAGE);
		}
	}

}

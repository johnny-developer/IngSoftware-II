package sigestor.utilerias;

import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.Writer;
import java.util.ArrayList;
import com.csvreader.CsvReader;
import com.csvreader.CsvWriter;
import sigestor.dominio.Participante;
import sigestor.excepcion.ExcepcionUtilerias;

/**
 * Encargada de validar el formato y leer la lista de participantes de un
 * archivo CSV, as� como generar una plantilla en un archivo Csv.
 * 
 * @version 23/03/2022
 * 
 * @author Jonathan Eduardo Ibarra Mart�nez.
 * @author Eder Euclides Dionisio Diaz.
 * @author Hernan Sesai Lopez Aragon.
 *
 */
public class UtileriasListaParticipantes {

	/**
	 * Encargado de validar el formato de un archivo CSV.
	 * 
	 * @param rutaArchivo
	 *            Contiene la ruta del archivo CSV.
	 * @return <tt>true</tt> Si el formato del archivo es correcto, <tt>false</tt>
	 *         en caso contrario.
	 */
	private static boolean validarListaParticipantes(String rutaArchivo) throws ExcepcionUtilerias {
		boolean formatoCorrecto = false;
		try {
			CsvReader csv = new CsvReader(new FileReader(rutaArchivo));
			csv.readHeaders();
			if (csv.getHeaderCount() == 2 && csv.getHeader(0).equals("Nombre") && csv.getHeader(1).equals("Puntaje")) {
				formatoCorrecto = true;
			} else {
				formatoCorrecto = false;
			}
		} catch (IOException e) {
			throw new ExcepcionUtilerias(ExcepcionUtilerias.MENSAJE_EXCEPCION_LEER_ARCHIVO_CSV);
		}
		return formatoCorrecto;
	}

	public static ArrayList<Participante> leerListaParticipantes(String rutaArchivo) throws ExcepcionUtilerias {
		if (validarListaParticipantes(rutaArchivo)) {
			// TODO lanzar excepci�n
		}
		// TODO crear un array de participantes (sin n�mero) con los datos del archivo
		// csv
		return null;
	}

	/**
	 * Genera una plantilla con extencion CSV con los datos de los participantes.
	 * 
	 * @param rutaDestino
	 *            Indica la ruta del archivo donde se guardara la plantilla.
	 * @throws ExcepcionUtilerias
	 *             Si ocurre un error al escribir en el archivo CSV.
	 */
	public static void escribirPlantilla(String rutaDestino) throws ExcepcionUtilerias {
		try {
			Writer writer;
			writer = new FileWriter(rutaDestino, false);
			CsvWriter writerCsv = new CsvWriter(writer, ',');
			writerCsv.write("Nombre ");
			writerCsv.write("Puntaje ");
			writerCsv.endRecord();
			writerCsv.close();
		} catch (IOException e) {
			throw new ExcepcionUtilerias(ExcepcionUtilerias.MENSAJE_EXCEPCION_ESCRIBIR_PLANTILLA_CSV);
		}
	}
}
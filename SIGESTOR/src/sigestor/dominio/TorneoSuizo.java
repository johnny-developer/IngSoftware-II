package sigestor.dominio;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashSet;
import java.util.List;
import sigestor.bd.BaseDatosCiclo;
import sigestor.bd.BaseDatosEncuentro;
import sigestor.bd.BaseDatosParticipante;
import sigestor.bd.BaseDatosTorneo;
import sigestor.excepcion.ExcepcionBaseDatos;
import sigestor.excepcion.ExcepcionBaseDatosCiclo;
import sigestor.excepcion.ExcepcionBaseDatosEncuentro;
import sigestor.excepcion.ExcepcionBaseDatosParticipante;
import sigestor.excepcion.ExcepcionBaseDatosTorneo;
import sigestor.excepcion.ExcepcionCapturarResultados;

/**
 * Sirve para realizar las operaciones del torneo, como: crear los ciclos seg�n
 * lo establecido en el sistema suizo, los encuentros seg�n el sistema suizo,
 * desempatar jugadores y realizar reportes. <code>AlgoritmoTorneo</code>.
 * <p>
 * 
 * @version 02/06/2023
 * 
 * @author Jonathan Eduardo Ibarra Mart�nez
 * @author Alicia Adriana Clemente Hernandez
 * @author Luis Fernando de la Cruz L�pez
 * @author Luis Antonio Ruiz Sierra
 * @author Victor Triste P�rez
 * 
 * @see AlgoritmoTorneo
 */
public class TorneoSuizo extends AlgoritmoTorneo {

	/**
	 * Inicializa las variables con un valor por defecto y asigna a la variable
	 * <code>torneo</code> el torneo recibido.
	 * 
	 * @param torneo Contiene los datos generales del torneo.
	 */
	public TorneoSuizo(Torneo torneo) {
		super(torneo);
	}

	/**
	 * Obtiene la cantidad m�xima de ciclos del torneo seg�n el algoritmo general
	 * del sistema suizo.
	 * 
	 * @param numeroParticipantes Cantidad de participantes inscritos en el torneo.
	 * @return El n�mero m�ximo de ciclos que tendr� el torneo.
	 */
	@Override
	public int calcularNumeroCiclos(int numeroParticipantes) {
		return (int) (Math.log(numeroParticipantes) / Math.log(2));
	}

	/**
	 * Genera inicialmente los encuentros del primer ciclo y despu�s los encuentros
	 * del siguiente ciclo una vez terminado el ciclo anterior.
	 */
	@Override
	public void realizarEncuentros() throws ExcepcionBaseDatos, ExcepcionBaseDatosEncuentro, ExcepcionBaseDatosCiclo {
		try {
			if (torneo.getCicloActual() < torneo.getAlgoritmoTorneo().getNumeroCiclos()) {
				torneo.setCicloActual(torneo.getCicloActual() + 1);
				BaseDatosCiclo bdc = new BaseDatosCiclo(torneo.getNombreArchivo());

				Ciclo ciclo = new Ciclo(torneo, torneo.getCicloActual());

				bdc.insertarCiclo(ciclo);
				if (torneo.getCicloActual() > 1) {
					encararParticipantesCiclosPosteriores(ciclo);
				} else {
					encararParticipantesPrimerCiclo(ciclo);
				}
				torneo.getAlgoritmoTorneo().getCiclos().add(ciclo);
				try {
					BaseDatosTorneo bdt = new BaseDatosTorneo(torneo.getNombreArchivo());
					bdt.actualizarCicloActual(torneo);
				} catch (ExcepcionBaseDatos | ExcepcionBaseDatosTorneo e) {

				}
			}
		} catch (ExcepcionCapturarResultados | ExcepcionBaseDatosParticipante e1) {

		}

	}

	/**
	 * Aplica el/los criterio(s) de desempate(s) establecidos en
	 * <code>CriteriosDesempate</code>.
	 */
	@Override
	public void desempatarParticipantes() {
		Desempate desempate;
		Participante participanteGanador;
		ArrayList<String> criterios = torneo.getCriteriosDesempate().getListaCriteriosSeleccionados();

		ArrayList<Participante> participantes = torneo.getListaParticipantes();
		for (Participante p1 : participantes) {
			for (Participante p2 : participantes) {
				if (p1.getPuntajeAcumuladoParticipante() == p2.getPuntajeAcumuladoParticipante()) {
					cicloromper: {
						for (String criterio : criterios) {
							switch (criterio) {
							case "Encuentro directo":
								desempate = new DesempateEncuentroDirecto();
								participanteGanador = desempate.desempatar(p1, p2, participantes,
										obtenerEncuentrosTotales(), torneo);
								if (participanteGanador != null) {
									participantes = intercambiarPosiciones(p1.getNumeroParticipante(),
											p2.getNumeroParticipante(), participanteGanador.getNumeroParticipante());
									break cicloromper;
								}
								break;
							case "Sistema Koya":
								desempate = new DesempateSistemaKoya();
								participanteGanador = desempate.desempatar(p1, p2, participantes,
										obtenerEncuentrosTotales(), torneo);
								if (participanteGanador != null) {
									participantes = intercambiarPosiciones(p1.getNumeroParticipante(),
											p2.getNumeroParticipante(), participanteGanador.getNumeroParticipante());
									break cicloromper;
								}
								break;
							case "Buchholz":
								desempate = new DesempateBuchholz();
								participanteGanador = desempate.desempatar(p1, p2, participantes,
										obtenerEncuentrosTotales(), torneo);
								if (participanteGanador != null) {
									participantes = intercambiarPosiciones(p1.getNumeroParticipante(),
											p2.getNumeroParticipante(), participanteGanador.getNumeroParticipante());
									break cicloromper;
								}
								break;
							case "Sonnerborn-Berger":
								desempate = new DesempateSonnebornBerger();
								participanteGanador = desempate.desempatar(p1, p2, participantes,
										obtenerEncuentrosTotales(), torneo);
								if (participanteGanador != null) {
									participantes = intercambiarPosiciones(p1.getNumeroParticipante(),
											p2.getNumeroParticipante(), participanteGanador.getNumeroParticipante());
									break cicloromper;
								}
								break;
							case "Encuentros ganados":
								desempate = new DesempateEncuentrosGanados();
								participanteGanador = desempate.desempatar(p1, p2, participantes,
										obtenerEncuentrosTotales(), torneo);
								if (participanteGanador != null) {
									participantes = intercambiarPosiciones(p1.getNumeroParticipante(),
											p2.getNumeroParticipante(), participanteGanador.getNumeroParticipante());
									break cicloromper;
								}
								break;
							case "Diferencia de marcadores":
								desempate = new DesempateDiferenciaMarcadores();
								participanteGanador = desempate.desempatar(p1, p2, participantes,
										obtenerEncuentrosTotales(), torneo);
								if (participanteGanador != null) {
									participantes = intercambiarPosiciones(p1.getNumeroParticipante(),
											p2.getNumeroParticipante(), participanteGanador.getNumeroParticipante());
									break cicloromper;
								}
								break;
							case "Marcador a favor":
								desempate = new DesempateMarcadorFavor();
								participanteGanador = desempate.desempatar(p1, p2, participantes,
										obtenerEncuentrosTotales(), torneo);
								if (participanteGanador != null) {
									participantes = intercambiarPosiciones(p1.getNumeroParticipante(),
											p2.getNumeroParticipante(), participanteGanador.getNumeroParticipante());
									break cicloromper;
								}
								break;
							case "Marcador en contra":
								desempate = new DesempateMarcadorContra();
								participanteGanador = desempate.desempatar(p1, p2, participantes,
										obtenerEncuentrosTotales(), torneo);
								if (participanteGanador != null) {
									participantes = intercambiarPosiciones(p1.getNumeroParticipante(),
											p2.getNumeroParticipante(), participanteGanador.getNumeroParticipante());
									break cicloromper;
								}
								break;
							default: // no se ha seleccionado ning�n criterio
							}

						}
					}
				}
			}
		}
		torneo.setListaParticipantes(participantes);
	}

	/**
	 * Obtiene todos los encuentros que ya hayan sido jugados en el torneo.
	 * 
	 * @return lista de encuentros jugados.
	 */
	private ArrayList<Encuentro> obtenerEncuentrosTotales() {
		ArrayList<Encuentro> encuentrosTotales = new ArrayList<Encuentro>();

		ArrayList<Ciclo> ciclos = torneo.getAlgoritmoTorneo().getCiclos();
		for (Ciclo ciclo : ciclos) {
			ArrayList<Encuentro> encuentros = ciclo.getEncuentroParticipantes();
			for (Encuentro encuentro : encuentros) {
				encuentrosTotales.add(encuentro);
			}
		}
		return encuentrosTotales;
	}

	/**
	 * Intercambia las posiciones de 2 jugadores empatados si el ganador est� una
	 * posici�n abajo del jugador con quien empat�, de lo contrario no realiza
	 * ning�n movimiento.
	 * 
	 * @param numeroP1    Primer participante empatado.
	 * @param numP2       Segundo participante empatado.
	 * @param numPGanador El participante que obtuvo m�s puntaje con el criterio de
	 *                    desempate aplicado.
	 * @return Lista de participantes ordenada.
	 */
	private ArrayList<Participante> intercambiarPosiciones(int numeroP1, int numP2, int numPGanador) {

		ArrayList<Participante> participantes = torneo.getListaParticipantes();

		boolean encontrado = false;
		int index = 0;

		for (Participante p : participantes) {
			if (numeroP1 == p.getNumeroParticipante() || numP2 == p.getNumeroParticipante()) {
				if (encontrado && p.getNumeroParticipante() == numPGanador) {
					Collections.swap(participantes, index, index - 1);
				}
				encontrado = true;
			}
			index++;
		}
		return participantes;
	}

	/**
	 * Realiza los encuentros del primer ciclo.
	 * 
	 * @param ciclo Ciclo a realizar.
	 * @throws ExcepcionCapturarResultados    Si ocurre un error con el objeto
	 *                                        <code>Encuentros</code>.
	 * @throws ExcepcionBaseDatos             Si ocurre un problema con la base de
	 *                                        datos.
	 * @throws ExcepcionBaseDatosEncuentro    Si ocurre un problema al insertar en
	 *                                        la tabla <code>encuentros</code>.
	 * @throws ExcepcionBaseDatosParticipante Si ocurre un problema al actualizar un
	 *                                        participante en la tabla
	 *                                        <code>participantes</code>.
	 */
	private void encararParticipantesPrimerCiclo(Ciclo ciclo) throws ExcepcionCapturarResultados, ExcepcionBaseDatos,
			ExcepcionBaseDatosEncuentro, ExcepcionBaseDatosParticipante {
		BaseDatosEncuentro bde = new BaseDatosEncuentro(torneo.getNombreArchivo());
		BaseDatosParticipante bdp = new BaseDatosParticipante(torneo.getNombreArchivo());
		ArrayList<Participante> participantes = torneo.getListaParticipantes();
		ArrayList<Encuentro> encuentros = new ArrayList<Encuentro>();
		int mitad = participantes.size() / 2;
		Participante sinEncuentro = null;

		for (Participante p : participantes) {
			if (p.getNombreParticipante()
					.equalsIgnoreCase(torneo.getDatosPersonalizacion().getNombreParticipanteSinEncuentro())) {
				sinEncuentro = p;
				break;
			}
		}
		if (sinEncuentro != null) {
			participantes.remove(sinEncuentro);
			participantes.add(sinEncuentro);
			mitad = mitad - 1;
		}

		for (int i = 1; i <= mitad; i++) {
			encuentros.add(new Encuentro(i, participantes.get(i - 1).getNumeroParticipante(),
					participantes.get(i - 1 + mitad).getNumeroParticipante(), this.getTorneo().getFechaInicioTorneo()));
			bde.insertarEncuentro(encuentros.get(i - 1), ciclo);
			bdp.actualizarResultadoParticipante(participantes.get(i - 1), ciclo);
			bdp.actualizarResultadoParticipante(participantes.get(i + mitad - 1), ciclo);
		}

		if (sinEncuentro != null) {
			encuentros.add(new Encuentro(mitad + 1, participantes.get(participantes.size() - 2).getNumeroParticipante(),
					participantes.get(participantes.size() - 1).getNumeroParticipante(),
					this.getTorneo().getFechaInicioTorneo()));
			bde.insertarEncuentro(encuentros.get(mitad), ciclo);
			bdp.actualizarResultadoParticipante(participantes.get(participantes.size() - 2), ciclo);
			bdp.actualizarResultadoParticipante(participantes.get(participantes.size() - 1), ciclo);
		}
		ciclo.setEncuentroParticipantes(encuentros);
	}

	/**
	 * Realiza los encuentros de un ciclo posterior al primer ciclo.
	 * 
	 * @param ciclo Recibe el objeto <code>Ciclo</code> para guardar los encuentros
	 *              a realizar.
	 * @throws ExcepcionCapturarResultados    Si ocurre un error con el objeto
	 *                                        <code>Encuentros</code>.
	 * @throws ExcepcionBaseDatos             Si ocurre un problema con la base de
	 *                                        datos.
	 * @throws ExcepcionBaseDatosEncuentro    Si ocurre un problema al insertar en
	 *                                        la tabla <code>encuentros</code>.
	 * @throws ExcepcionBaseDatosParticipante Si ocurre un problema al actualizar un
	 *                                        participante en la tabla
	 *                                        <code>participantes</code>.
	 */
	private void encararParticipantesCiclosPosteriores(Ciclo ciclo) throws ExcepcionCapturarResultados,
			ExcepcionBaseDatos, ExcepcionBaseDatosEncuentro, ExcepcionBaseDatosParticipante {
		BaseDatosEncuentro bde = new BaseDatosEncuentro(torneo.getNombreArchivo());
		BaseDatosParticipante bdp = new BaseDatosParticipante(torneo.getNombreArchivo());
		ArrayList<Participante> participantes = torneo.getListaParticipantes();
		ArrayList<Encuentro> encuentros = new ArrayList<Encuentro>();
		Participante sinEncuentro = null;

		HashSet<Float> puntajes = new HashSet<>();

		Collections.sort(participantes, new Comparator<Participante>() {
			public int compare(Participante p1, Participante p2) {
				return Float.compare(p2.getPuntajeAcumuladoParticipante(), p1.getPuntajeAcumuladoParticipante());
			}
		});

		for (Participante p : participantes) {
			puntajes.add(p.getPuntajeAcumuladoParticipante());
			if (p.getNombreParticipante()
					.equalsIgnoreCase(torneo.getDatosPersonalizacion().getNombreParticipanteSinEncuentro())) {
				sinEncuentro = p;
				break;
			}
		}
		if (sinEncuentro != null) {
			participantes.remove(sinEncuentro);
			ArrayList<Participante> ultimosJugadores = new ArrayList<>();
			List<Float> listaPuntajes = new ArrayList<>(puntajes);
			Collections.sort(listaPuntajes, new Comparator<Float>() {
				public int compare(Float f1, Float f2) {
					return Float.compare(f2, f1);
				}
			});
			for (Participante p : participantes) {
				if (p.getPuntajeAcumuladoParticipante() == listaPuntajes.get(puntajes.size() - 1)) {
					ultimosJugadores.add(p);
				}
			}
			for (Participante p : ultimosJugadores) {
				participantes.remove(p);
			}
			Collections.reverse(ultimosJugadores);
			for (Participante p : ultimosJugadores) {
				participantes.add(p);
			}
			participantes.add(sinEncuentro);
		}

		int num = 1;
		int pos = 0;
		while (pos < participantes.size()) {
			float puntaje = participantes.get(pos).getPuntajeAcumuladoParticipante();
			int fin = pos + 1;
			while (fin < participantes.size() && participantes.get(fin).getPuntajeAcumuladoParticipante() == puntaje) {
				fin++;
			}
			int cont = (fin - pos);
			int mitad = (cont) / 2;
			if (cont % 2 != 0) {
				mitad = mitad + 1;
			}
			for (int j = 0; j < mitad; j++) {
				encuentros.add(new Encuentro(num, participantes.get(pos).getNumeroParticipante(),
						participantes.get(pos + mitad).getNumeroParticipante(),
						this.getTorneo().getFechaInicioTorneo()));
				bde.insertarEncuentro(encuentros.get(num - 1), ciclo);
				bdp.actualizarResultadoParticipante(participantes.get(pos), ciclo);
				bdp.actualizarResultadoParticipante(participantes.get(pos + mitad), ciclo);
				num++;
				pos++;

			}
			pos += mitad;
		}
		ciclo.setEncuentroParticipantes(encuentros);
	}

	/**
	 * Verifica si se han capturado todos los encuentros del ciclo.
	 * 
	 * @return <tt>true</tt> Si se han capturado todos los encuentros de un ciclo,
	 *         <tt>false</tt> de lo contrario.
	 */
	public boolean verificarEncuentros() {
		ArrayList<Encuentro> encuentros = torneo.getAlgoritmoTorneo().getCiclos().get(torneo.getCicloActual() - 1)
				.getEncuentroParticipantes();
		for (Encuentro e : encuentros) {
			if (e.getResultadoEncuentro() == Encuentro.SIN_JUGAR) {
				return false;
			}
		}
		return true;
	}

	/**
	 * Inicia un torneo suizo e inserta el numero de ciclos en la tabla
	 * <code>suizo</code>.
	 * 
	 * @throws ExcepcionBaseDatos          Si ocurre un problema con la base de
	 *                                     datos.
	 * @throws ExcepcionBaseDatosEncuentro Si ocurre un problema al insertar en la
	 *                                     tabla<code>encuentros</code>.
	 * @throws ExcepcionBaseDatosCiclo     Si ocurre un error al insertar en la
	 *                                     tabla <code>ciclos</code>.
	 * @throws ExcepcionBaseDatosTorneo    Si ocurre un error al insertar en la
	 *                                     tabla <code>suizo</code>.
	 */
	public void iniciarTorneo()
			throws ExcepcionBaseDatos, ExcepcionBaseDatosEncuentro, ExcepcionBaseDatosCiclo, ExcepcionBaseDatosTorneo {
		BaseDatosTorneo bdt = new BaseDatosTorneo(torneo.getNombreArchivo());
		bdt.insertarTorneoSuizo(this);
		this.setCiclos(new ArrayList<Ciclo>());
		torneo.setAlgoritmoTorneo(this);
		this.realizarEncuentros();
	}
}

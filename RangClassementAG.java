package fr.sedit.grh.coeur.ca.avg.model;
import fr.sedit.core.common.model.AbstractPersistentObjectNO;

/**
 * @author ROSTAIN Guilhem
 * @version 1.0
 * @created 16-janv.-2008 11:15:53
 */
public class RangClassementAG extends AbstractPersistentObjectNO {

	private static final long serialVersionUID = -2290244886916614639L;
	
	private String saisieLibre;
	private String valeur;

	/**
	 * Constructeur
	 */
	public RangClassementAG(){
		super();
	}

	/**
	 * @return the saisieLibre
	 */
	public String getSaisieLibre() {
		return saisieLibre;
	}

	/**
	 * @param saisieLibre the saisieLibre to set
	 */
	public void setSaisieLibre(String saisieLibre) {
		this.saisieLibre = saisieLibre;
	}

	/**
	 * @return the valeur
	 */
	public String getValeur() {
		return valeur;
	}

	/**
	 * @param valeur the valeur to set
	 */
	public void setValeur(String valeur) {
		this.valeur = valeur;
	}

}
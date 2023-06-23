package fr.sedit.grh.coeur.ca.avg.model;
import java.util.Date;

import fr.sedit.core.common.model.AbstractPersistentObjectNO;
import fr.sedit.grh.coeur.ca.par.model.ParamAppreciationAvis;
import fr.sedit.grh.coeur.cs.model.Agent;

/**
 * @author ROSTAIN Guilhem
 * @version 1.0
 * @created 16-janv.-2008 11:15:51
 */
public class AvisAG extends AbstractPersistentObjectNO {

	private static final long serialVersionUID = -2555892337307931858L;
	
	private String commentaire;
	private Date dateAvis;
	private Integer ordre;
	private String qualite;
	private String valeurAppreciation;
	private ParamAppreciationAvis appreciation;
	private PropositionAG propositionAG;
	private Agent agentResponsable;

	/**
	 * Constructeur
	 */
	public AvisAG(){
		super();
	}

	/**
	 * @return the commentaire
	 */
	public String getCommentaire() {
		return commentaire;
	}

	/**
	 * @param commentaire the commentaire to set
	 */
	public void setCommentaire(String commentaire) {
		this.commentaire = commentaire;
	}

	/**
	 * @return the dateAvis
	 */
	public Date getDateAvis() {
		return dateAvis;
	}

	/**
	 * @param dateAvis the dateAvis to set
	 */
	public void setDateAvis(Date dateAvis) {
		this.dateAvis = dateAvis;
	}

	/**
	 * @return the ordre
	 */
	public Integer getOrdre() {
		return ordre;
	}

	/**
	 * @param ordre the ordre to set
	 */
	public void setOrdre(Integer ordre) {
		this.ordre = ordre;
	}

	/**
	 * @return the qualite
	 */
	public String getQualite() {
		return qualite;
	}

	/**
	 * @param qualite the qualite to set
	 */
	public void setQualite(String qualite) {
		this.qualite = qualite;
	}

	/**
	 * @return the valeurAppreciation
	 */
	public String getValeurAppreciation() {
		return valeurAppreciation;
	}

	/**
	 * @param valeurAppreciation the valeurAppreciation to set
	 */
	public void setValeurAppreciation(String valeurAppreciation) {
		this.valeurAppreciation = valeurAppreciation;
	}

	/**
	 * @return the appreciation
	 */
	public ParamAppreciationAvis getAppreciation() {
		return appreciation;
	}

	/**
	 * @param appreciation the appreciation to set
	 */
	public void setAppreciation(ParamAppreciationAvis appreciation) {
		this.appreciation = appreciation;
	}

	/**
	 * @return the propositionAG
	 */
	public PropositionAG getPropositionAG() {
		return propositionAG;
	}

	/**
	 * @param propositionAG the propositionAG to set
	 */
	public void setPropositionAG(PropositionAG propositionAG) {
		this.propositionAG = propositionAG;
	}

	public Agent getAgentResponsable() {
		return agentResponsable;
	}

	public void setAgentResponsable(Agent agentResponsable) {
		this.agentResponsable = agentResponsable;
	}

}
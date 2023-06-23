package fr.sedit.grh.coeur.ca.avg.services.impl;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.BooleanUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.hibernate.Hibernate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import fr.bl.shared.grh.coeur.dto.CollectiviteNumerotationArreteParamDTO;
import fr.sedit.core.exception.SeditException;
import fr.sedit.core.hibernate.IDaoCursor;
import fr.sedit.core.tools.UtilsDate;
import fr.sedit.grh.ca.CarriereException;
import fr.sedit.grh.ca.avg.model.JustificatifRetardAG;
import fr.sedit.grh.ca.avg.model.dto.AncienneteDTO;
import fr.sedit.grh.ca.avg.model.dto.AvisAGDTO;
import fr.sedit.grh.ca.avg.model.dto.DetailAvancementAGDTO;
import fr.sedit.grh.ca.avg.model.dto.PropositionAGAExporterDTO;
import fr.sedit.grh.ca.avg.model.dto.PropositionAGDTO;
import fr.sedit.grh.ca.avg.model.dto.PropositionNonPromouvableAGDTO;
import fr.sedit.grh.ca.avg.model.dto.PropositionsConcoursAgentDTO;
import fr.sedit.grh.ca.avg.services.rules.AbstractRuleAvg;
import fr.sedit.grh.ca.avg.services.rules.AbstractRuleAvgAnciennete;
import fr.sedit.grh.ca.avg.services.rules.AbstractRuleAvgExamenProfessionnel;
import fr.sedit.grh.ca.avg.services.rules.IRuleAvg;
import fr.sedit.grh.ca.avg.services.rules.RuleAvgAnd;
import fr.sedit.grh.ca.avg.services.rules.RuleAvgException;
import fr.sedit.grh.ca.avg.services.rules.RuleAvgFormationSPP;
import fr.sedit.grh.ca.avg.services.rules.RuleAvgLineRule;
import fr.sedit.grh.ca.avg.services.rules.RuleAvgOr;
import fr.sedit.grh.ca.par.model.enums.EnumModeCreation;
import fr.sedit.grh.coeur.ab.dao.IDaoRegimeAbsence;
import fr.sedit.grh.coeur.ca.avg.dao.IDaoPropositionAG;
import fr.sedit.grh.coeur.ca.avg.model.ConditionDeProposition;
import fr.sedit.grh.coeur.ca.avg.model.DetailCondition;
import fr.sedit.grh.coeur.ca.avg.model.PropositionAG;
import fr.sedit.grh.coeur.ca.avg.model.TableauAG;
import fr.sedit.grh.coeur.ca.avg.model.enums.EnumTypeAvancementAG;
import fr.sedit.grh.coeur.ca.avg.model.enums.EnumTypePropositionAG;
import fr.sedit.grh.coeur.ca.avg.model.enums.EnumVerifierAG;
import fr.sedit.grh.coeur.ca.avg.services.IServicePropositionAG;
import fr.sedit.grh.coeur.ca.par.dao.IDaoLigneConditionAG;
import fr.sedit.grh.coeur.ca.par.dao.IDaoParamCaOrga;
import fr.sedit.grh.coeur.ca.par.dao.IDaoValeurFonctionCondition;
import fr.sedit.grh.coeur.ca.par.model.LigneConditionAG;
import fr.sedit.grh.coeur.ca.par.model.ParamCaGrade;
import fr.sedit.grh.coeur.ca.par.model.ParamCaOrga;
import fr.sedit.grh.coeur.ca.par.model.ValeurFonctionCondition;
import fr.sedit.grh.coeur.ca.par.model.enums.EnumDateValeurFonction;
import fr.sedit.grh.coeur.ca.par.model.enums.EnumOperateurConditionAG;
import fr.sedit.grh.coeur.cs.dao.IDaoAgent;
import fr.sedit.grh.coeur.cs.dao.IDaoCarriere;
import fr.sedit.grh.coeur.cs.dao.IDaoCarriereSynthetique;
import fr.sedit.grh.coeur.cs.dao.IDaoCollectivite;
import fr.sedit.grh.coeur.cs.dao.IDaoService;
import fr.sedit.grh.coeur.cs.model.Agent;
import fr.sedit.grh.coeur.cs.model.CadreEmploi;
import fr.sedit.grh.coeur.cs.model.Carriere;
import fr.sedit.grh.coeur.cs.model.CarriereSynthetique;
import fr.sedit.grh.coeur.cs.model.Categorie;
import fr.sedit.grh.coeur.cs.model.Contrat;
import fr.sedit.grh.coeur.cs.model.FicheGradeEmploi;
import fr.sedit.grh.coeur.cs.model.FichePositionAdmin;
import fr.sedit.grh.coeur.cs.model.Filiere;
import fr.sedit.grh.coeur.cs.model.Grade;
import fr.sedit.grh.coeur.cs.model.Imprime;
import fr.sedit.grh.coeur.cs.model.PositionAdmin;
import fr.sedit.grh.coeur.cs.model.dto.AgentDTO;
import fr.sedit.grh.coeur.cs.model.dto.CarrierePropositionAGDTO;
import fr.sedit.grh.coeur.cs.model.dto.ServiceDetailDTO;
import fr.sedit.grh.coeur.cs.model.parametrage.Collectivite;
import fr.sedit.grh.coeur.cs.model.parametrage.Organisme;
import fr.sedit.grh.coeur.cs.model.parametrage.Service;
import fr.sedit.grh.coeur.cs.model.views.ViewHierarchieService;
import fr.sedit.grh.coeur.cs.services.IServiceConcoursAgent;
import fr.sedit.grh.coeur.cs.services.IServiceExamenProAgent;
import fr.sedit.grh.coeur.cs.services.IServiceServiceDetail;
import fr.sedit.grh.coeur.gc.model.eva.CompteRenduEntretien;
import fr.sedit.grh.coeur.gc.model.eva.FicheEvaluation;
import fr.sedit.grh.coeur.gc.services.eval.IServiceFicheEvaluation;
import fr.sedit.grh.coeur.sm.dao.IDaoModuleSMRH;
import fr.sedit.grh.coeur.sm.services.IServiceModuleSMRH;
import fr.sedit.grh.common.rules.AbstractRuleComposite;
import fr.sedit.sedit.common.model.Notice;
import fr.sedit.sedit.sm.dao.IDaoNotice;
/*
 * Generated Implementation
 */
@Transactional
public class ServicePropositionAG implements IServicePropositionAG {

	private static final Log log = LogFactory.getLog(ServicePropositionAG.class);

	// ---------- Debut Injection Spring ----------
	private IDaoPropositionAG daoPropositionAG;
    public void setDaoPropositionAG(IDaoPropositionAG daoPropositionAG) {
    	this.daoPropositionAG = daoPropositionAG;
    }
    
    private IDaoLigneConditionAG daoLigneConditionAG;
	public void setDaoLigneConditionAG(IDaoLigneConditionAG daoLigneConditionAG) {
		this.daoLigneConditionAG = daoLigneConditionAG;
	}
    
	private IDaoCarriereSynthetique daoCarriereSynthetique;
	public void setDaoCarriereSynthetique(IDaoCarriereSynthetique daoCarriereSynthetique) {
		this.daoCarriereSynthetique = daoCarriereSynthetique;
	}
    
	private IDaoAgent daoAgent;
	public void setDaoAgent(IDaoAgent daoAgent) {
		this.daoAgent = daoAgent;
	}
    
	private IDaoValeurFonctionCondition daoValeurFonctionCondition;
	public void setDaoValeurFonctionCondition(IDaoValeurFonctionCondition daoValeurFonctionCondition) {
		this.daoValeurFonctionCondition = daoValeurFonctionCondition;
	}
    
	private IServiceServiceDetail serviceServiceDetail = null;
    public void setServiceServiceDetail(IServiceServiceDetail serviceServiceDetail){
        this.serviceServiceDetail = serviceServiceDetail;
    }
    
	private IDaoCollectivite daoCollectivite = null;
	public void setDaoCollectivite(IDaoCollectivite daoCollectivite){
		this.daoCollectivite = daoCollectivite;
	}
    
	private IDaoCarriere daoCarriere;
	public void setDaoCarriere(IDaoCarriere daoCarriere) {
        this.daoCarriere = daoCarriere;
	}
    
    private IDaoRegimeAbsence daoRegimeAbsence;
    public void setDaoRegimeAbsence(IDaoRegimeAbsence daoRegimeAbsence) {
        this.daoRegimeAbsence = daoRegimeAbsence;
    }
    
    private IDaoNotice daoNotice;
    public void setDaoNotice(IDaoNotice daoNotice) {
        this.daoNotice = daoNotice;
    }
    
    private IDaoParamCaOrga daoParamCaOrga;
    public void setDaoParamCaOrga(IDaoParamCaOrga daoParamCaOrga) {
        this.daoParamCaOrga = daoParamCaOrga;
    }
    
    @Autowired(required=false)
    @Qualifier("serviceModuleSMRH")
    private IServiceModuleSMRH serviceModuleSMRH;
    
    @Autowired(required=false)
    @Qualifier("daoService")
    private IDaoService daoService;
    
    @Autowired
    private IServiceExamenProAgent serviceExamenProAgent;
    
    @Autowired
    private IServiceConcoursAgent serviceConcoursAgent;
    
    // ESRH-6729
    @Autowired 
    @Qualifier("serviceFicheEvaluation")
    private IServiceFicheEvaluation serviceFicheEvaluation;
    // ---------- Fin Injection Spring ----------


	/**
	 * @see fr.sedit.grh.coeur.ca.avg.services.IServicePropositionAG#findById(java.lang.Integer)
	 */
	@Override
	@Transactional(propagation=Propagation.SUPPORTS,readOnly=true)
	public PropositionAG findById(Integer propositionId) {
		return this.daoPropositionAG.findById(propositionId, false);
	}	
	
	/**
	 * @see fr.sedit.grh.coeur.ca.avg.services.IServicePropositionAG#loadAndBuildRuleList(fr.sedit.grh.coeur.cs.model.parametrage.Organisme, java.util.Date, java.util.Date, fr.sedit.grh.coeur.ca.avg.model.enums.EnumTypeAvancementAG)
	 */
	@Override
	@SuppressWarnings("unchecked")
	@Transactional(propagation=Propagation.SUPPORTS,readOnly=true)
	public Set<IRuleAvg> loadAndBuildRuleList(Organisme organisme, Date dateDebut, Date dateFin, final List<EnumTypeAvancementAG> listTypeAvancement) {
		// chargement des rêgles sur les grades
		List<LigneConditionAG> listLignes= daoLigneConditionAG.loadLignesConditions(organisme, dateDebut, dateFin, listTypeAvancement, null);
		if (listLignes==null || listLignes.isEmpty()) 
			throw new CarriereException(CarriereException.Type.NO_RULES_FOR_PERIOD, dateDebut, dateFin);

		// construction de la liste de regles par grade (ParamCAGrade)
		Set<IRuleAvg> rules= new HashSet<IRuleAvg>(listLignes.size()/3);		// moyenne de 3 lignes / grade
		
		ParamCaGrade currentGrade= null;
		List<IRuleAvg> listRules= null;
		EnumOperateurConditionAG lastOp= new EnumOperateurConditionAG();
		
		// itération sur les "lignes" de conditions (ordonnées par grade cible)
		final RuleAvgException ruleException= new RuleAvgException(SeditException.Type.MULTIPLE_EXCEPTION);
		Iterator<LigneConditionAG> it= listLignes.iterator();
		while (it.hasNext()){
			LigneConditionAG line= it.next();

			ParamCaGrade lineGrade= line.getParamCAGrade();
			if (!lineGrade.equals(currentGrade)) {
				// start a new rule list for a target grade (OR rule)
				currentGrade= lineGrade;
				listRules= new ArrayList<IRuleAvg>(3);
				rules.add(new RuleAvgOr(lineGrade, listRules));
			}

			// build & add rule to current list
			if (line.getListValeurCritereCondition()==null)
				log.warn("No conditions for line "+line);
			else{
				IRuleAvg newLineRules = buildRules(null, line.getListValeurCritereCondition().iterator(), ruleException, lastOp);
				if (newLineRules==null) {
					log.warn("Null conditions for line "+line);
				} else {
					// MANTIS 16759 SROM 01/2013 : vérification du bon paramétrage des lignes de condition (pas de OU ou de ET seuls)
					try {
						checkRule(newLineRules);
					} catch (RuleAvgException e) {
						throw new RuleAvgException(RuleAvgException.Type.MAUVAISE_CONSTRUCTION, line.getParamCAGrade().getGrade().toString());
					}
					
				    // GIFT IN 814 - LDEC - 17/05/2010 - Lorsqu'une condition doit être appréciée au 01/01 de l'année de réussite à un examen, la règle n'est pas
                    // évaluée correctement si la condition de réussite à l'examen n'a pas encore été évaluée.
                    if (newLineRules instanceof AbstractRuleComposite<?>) {
                        this.organizeForReussiteExamen(((AbstractRuleComposite<IRuleAvg>)newLineRules).getListRules(), ruleException);
                    }
                    
					// associate newLineRule to current line through RuleAvgLineRule
					listRules.add(new RuleAvgLineRule(newLineRules, line));
				}
			}
		}
		if (ruleException.hasExceptions()) {
            throw ruleException;
        }
		// debug display of rules
    	if (log.isDebugEnabled())
	    	for(IRuleAvg r: rules) log.debug("RULES="+ r.toString());
		
		return rules;
	}
	
	/**
	 * @see fr.sedit.grh.coeur.ca.avg.services.IServicePropositionAG#loadAndBuildRuleList(fr.sedit.grh.coeur.cs.model.parametrage.Organisme, java.util.Date, java.util.Date, fr.sedit.grh.coeur.cs.model.Grade, EnumTypeAvancementAG)
	 */
	@Override
	@SuppressWarnings("unchecked")
	@Transactional(propagation=Propagation.SUPPORTS,readOnly=true)
	public IRuleAvg loadAndBuildRuleList(Organisme organisme, Date dateDebut, Date dateFin, Grade gradeCible, EnumTypeAvancementAG typeAvancement) {
		// chargement des rêgles sur les grades
	    //MANTIS 11887 : on cherche les lignes de conditions pour un grade et un type d'avancement
	    List<EnumTypeAvancementAG> listTypeAvancement =null;
	    if (typeAvancement!=null){
	        listTypeAvancement = new ArrayList<EnumTypeAvancementAG>(1);
	        listTypeAvancement.add(typeAvancement);    
	    }
	    List<LigneConditionAG> listLignes= daoLigneConditionAG.loadLignesConditions(organisme, dateDebut, dateFin, listTypeAvancement, gradeCible);
        if (listLignes == null || listLignes.isEmpty()) {
            return null; // Pas de regle pour le grade et le type d'avancement demandé on retourne null
        }

		// construction de la liste de regles par grade (ParamCAGrade)
		List<IRuleAvg> listRules= new ArrayList<IRuleAvg>(3);
		IRuleAvg ruleGradeCible= new RuleAvgOr(listLignes.get(0).getParamCAGrade(), listRules);
		
		EnumOperateurConditionAG lastOp= new EnumOperateurConditionAG();
		
		// itération sur les "lignes" de conditions
		final RuleAvgException ruleException= new RuleAvgException(SeditException.Type.MULTIPLE_EXCEPTION);
		Iterator<LigneConditionAG> it= listLignes.iterator();
		while (it.hasNext()){
			LigneConditionAG line= it.next();

			// build & add rule
			if (line.getListValeurCritereCondition()==null)
				log.warn("No conditions for line "+line);
			else{
				IRuleAvg newLineRules = buildRules(null, line.getListValeurCritereCondition().iterator(), ruleException, lastOp);
				if (newLineRules==null)
					log.warn("Null conditions for line "+line);
				else{
					// MANTIS 16759 SROM 01/2013 : vérification du bon paramétrage des lignes de condition (pas de OU ou de ET seuls)
					try {
						checkRule(newLineRules);
					} catch (RuleAvgException e) {
						throw new RuleAvgException(RuleAvgException.Type.MAUVAISE_CONSTRUCTION, line.getParamCAGrade().getGrade().toString());
					}
					
					// GIFT IN 814 - LDEC - 17/05/2010 - Lorsqu'une condition doit être appréciée au 01/01 de l'année de réussite à un examen, la règle n'est pas
                    // évaluée correctement si la condition de réussite à l'examen n'a pas encore été évaluée.
                    if (newLineRules instanceof AbstractRuleComposite<?>) {
                        this.organizeForReussiteExamen(((AbstractRuleComposite<IRuleAvg>)newLineRules).getListRules(), ruleException);
                    }
					// associate newLineRule to current line through RuleAvgLineRule
					listRules.add(new RuleAvgLineRule(newLineRules, line));
				}
			}
		}
        if (ruleException.hasExceptions()) {
            throw ruleException;
        }

		// debug display of rules
    	log.debug("RULES="+ ruleGradeCible.toString());
		
		return ruleGradeCible;
	}
	
	/**
	 * Méthode récursive qui soulève une exception si une règle et vide.
	 * Cela correspond à un ET ou OU qui est mal construit.
	 * @param ruleToCheck
	 */
	private void checkRule(IRuleAvg ruleToCheck) {
		if (ruleToCheck == null) {
			throw new RuleAvgException(RuleAvgException.Type.MAUVAISE_CONSTRUCTION);
		}
		if (ruleToCheck instanceof RuleAvgAnd) {
			RuleAvgAnd ruleAnd = (RuleAvgAnd)ruleToCheck;
			for (IRuleAvg rule : ruleAnd.getListRules()) {
				checkRule(rule);
			}
		}
		if (ruleToCheck instanceof RuleAvgOr) {	
			RuleAvgOr ruleOr = (RuleAvgOr)ruleToCheck;
			for (IRuleAvg rule : ruleOr.getListRules()) {
				checkRule(rule);
			}
		}
	}
	
	/**
	 * Construction de l'arborescence de règles à partir d'une description sequentielle
	 * 
	 * @param ref référence à la ligne
	 * @param it itérateur sur les conditions de la ligne
	 * @return
	 */
	@SuppressWarnings("unchecked")
    private IRuleAvg buildRules(IRuleAvg ruleToBuild, Iterator<ValeurFonctionCondition> it, RuleAvgException ruleAvgException, EnumOperateurConditionAG lastOp){
		IRuleAvg lineRule= ruleToBuild;
		EnumOperateurConditionAG op= null;
		
		while(it.hasNext()){
			ValeurFonctionCondition vfc= it.next();
			// since list is sorted & built by hibernate, null values may be present
			if (vfc==null) continue;
			
			op= vfc.getOperateur();
			
			if (EnumOperateurConditionAG.ET.equals(op)){
				log.trace("start AND");
				if(lineRule==null){
					log.warn("Condition starting with AND");
				}else if(lineRule instanceof RuleAvgAnd){
					// do nothing
				}else if(lineRule instanceof RuleAvgOr){
					if (lastOp.getCode()==null){
						// from OR(...,R1),... to OR(...,AND(R1,...))
						List<IRuleAvg> lr= ((RuleAvgOr)lineRule).getListRules(); 
						if(!lr.isEmpty()){ 
							IRuleAvg r1= lr.remove(lr.size()-1);
							IRuleAvg r= buildRules(new RuleAvgAnd(r1), it, ruleAvgException, lastOp);
							((RuleAvgOr)lineRule).add(r);
						}
					}else{
						// from OR() to AND(OR(),...)
						lineRule= new RuleAvgAnd(lineRule, buildRules(null, it, ruleAvgException, lastOp));
					}
				}else{
					// from R to AND(R,...)
					lineRule= new RuleAvgAnd(lineRule);
				}
			}else if (EnumOperateurConditionAG.OU.equals(op)){
				log.trace("start OR");
				if(lineRule==null){
					log.warn("Condition starting with OR");
				}else if(lineRule instanceof RuleAvgAnd){
					// from AND() to OR(AND(),...)
					lineRule= new RuleAvgOr(lineRule);
					((RuleAvgOr)lineRule).add(buildRules(null, it, ruleAvgException, lastOp));
				}else if(lineRule instanceof RuleAvgOr){			// do nothing
				}else{
					// from R to OR(R,...)
					lineRule= new RuleAvgOr(lineRule);
				}
			}else if (EnumOperateurConditionAG.PARENTHESE_FERMANTE.equals(op)){
				log.trace("close PARENTHESIS");
				// end this condition
				//return lineRule;
				break;
			}else{
				IRuleAvg newRule= null;
				if (EnumOperateurConditionAG.PARENTHESE_OUVRANTE.equals(op)){
					log.trace("open PARENTHESIS");
					newRule= buildRules(null, it, ruleAvgException, lastOp);
					if (!EnumOperateurConditionAG.PARENTHESE_FERMANTE.equals(lastOp))
						log.warn("Parenthese fermante manquante");
				}else try {
					// regle de base (metafonction)
					newRule= AbstractRuleAvg.buildRule(vfc);
				}catch(SeditException se){
					log.error(se.getMessage());
				    ruleAvgException.addException(se);    
				}

				if (newRule!=null){
					if (lineRule==null){
						lineRule= newRule;
					}else if(lineRule instanceof AbstractRuleComposite<?>){
						((AbstractRuleComposite<IRuleAvg>)lineRule).add(newRule);
					}else{
						// from R1,R,... to AND(R1,R,...)
						// lineRule should be a simple rule, build a "AND" list
						lineRule= new RuleAvgAnd(lineRule);
						((RuleAvgAnd)lineRule).add(newRule);
					}
				}
			}
		}
		lastOp.setValue(op);
		return lineRule;
	}
	
	/**
     * Pour toutes les règles simple de la liste donnée : vérifier que si l'une d'elle doit être apprecié au 01/01 de l'année de réussite à un examen,
     * il existe placé avant dans la liste une regle de reussite à l'examen.<br>
     * Effectue cette vérification recursivement pour les regles composé (AND, OR)<br>
     * Si necessaire place la regle de reussite à l'examen juste avant celle qui en as besoin.
     * 
     * @param rules Liste de règle
     */
    @SuppressWarnings("unchecked")
    private void organizeForReussiteExamen(final List<IRuleAvg> rules, final RuleAvgException ruleException) {
        // Variable pour stocker une copie de la liste des règles afin de parcourir la copie pour ne pas etre géné lorsque l'on déplace des élement
        // dans l'original.
        final IRuleAvg[] tabRules = rules.toArray(new IRuleAvg[rules.size()]);
        
        // Recherche si une condition devant être apprécié au janvier de l'année de réussite à l'examen existe
        boolean conditionJanvierExamFinded = false;
        for (int i = 0 ; i < tabRules.length ; i++) {
            IRuleAvg rule = tabRules[i];
            // Si un And ou un Or
            if (rule instanceof AbstractRuleComposite<?>) {
                // Faire recusivement cette vérification
                this.organizeForReussiteExamen(((AbstractRuleComposite<IRuleAvg>) rule).getListRules(), ruleException);
            }
            // Sinon, seulement si une première condition devant être apprécié au janvier de l'année de réussite à l'examen n'as pas été trouvé
            else if(!conditionJanvierExamFinded) {
                final ValeurFonctionCondition valeurFonctionCondition = this.daoValeurFonctionCondition.findById(rule.getValeurFonctionConditionId(),
                        false);
                // Si il existe une condition devant être apprécié au janvier de l'année de réussite à l'examen
                if (EnumDateValeurFonction.JANVIER_EXAMEN.equals(valeurFonctionCondition.getDateValeur())) {
                    conditionJanvierExamFinded = true;
                    // Recherche si il existe une règle de réussite à l'examen
                    final int indexRuleExamen = this.indexOfRuleExamenProfessionnel(tabRules);
                    if (indexRuleExamen == -1) {
                    	String gradeCode = "<>";
                    	if(valeurFonctionCondition.getLigneConditionAG() != null) {
                            if(valeurFonctionCondition.getLigneConditionAG().getParamCAGrade() != null) {
                                if(valeurFonctionCondition.getLigneConditionAG().getParamCAGrade().getGrade() != null) {
                                    gradeCode = valeurFonctionCondition.getLigneConditionAG().getParamCAGrade().getGrade().getCode();
                                }
                            }
                        }
                        ruleException.addException(new RuleAvgException(RuleAvgException.Type.NO_EXAM_DATE, rule.getLibelle(), gradeCode));
                    }
                    // Si la règle de réussite à l'examen est situé après la règle qui en a besoin on la place avant
                    else if (indexRuleExamen > i) {
                        rules.remove(indexRuleExamen);
                        rules.add(i, tabRules[indexRuleExamen]);
                    }
                }
            }
        }
    }
    
    /**
     * Recherche directement dans la liste s'il existe une règle de réussite à l'examen. Si trouvé renvoi son index dans la liste sinon renvoi -1
     * 
     * @param tabRules
     * @return -1 si non trouvé sinon son index
     */
    private int indexOfRuleExamenProfessionnel(IRuleAvg[] tabRules) {
        for (int i = 0; i < tabRules.length; i++) {
            if (tabRules[i] instanceof AbstractRuleAvgExamenProfessionnel) {
                return i;
            }
        }
        return -1;
    }
    
	/**
	 * Création d'une proposition par grade cible & carriere
	 * 
	 * @param dateCalcul
	 * @param carriereDTO
	 * @param rule
	 * @return
	 */
	@Override
	@Transactional(propagation=Propagation.SUPPORTS,readOnly=true)
	public PropositionAG buildPropositionFromCalculs(boolean simulation, ParamCaOrga paramCaOrga, Date dateCalcul,CarrierePropositionAGDTO carriereDTO, IRuleAvg rule, TableauAG tableau, Date datePromotionTableau, Date dateDebut, Date dateFin){
		
		// id of top rule should be a paramCaGrade
		ParamCaGrade paramCaGrade= (ParamCaGrade)rule.getRef();
		EnumTypeAvancementAG typeAvancement= paramCaGrade.getTypeAvancement();
		FichePositionAdmin lastFichePositionAdmin = carriereDTO.getLastFichePositionAdmin();
		if (positionNAutorisePasAvancement(typeAvancement, lastFichePositionAdmin)) {
			return null;
		}
		
		// ******** nouvelle proposition ********
		PropositionAG proposition= new PropositionAG();
		// **** attributs minis
		proposition.setGradeCible(paramCaGrade.getGrade());
		proposition.setCarriere(carriereDTO.getCarriere());
		proposition.setCodeRegroupement(carriereDTO.getCodeRegroupement());
		proposition.setTableauAG(tableau);
		proposition.setDateCalcul(dateCalcul);
		try{
			// **** attributs
			proposition.setPromouvable(rule.isTrue());
			if (rule.isTrue()) proposition.setDateMiniAvancement(rule.getDateRealized());
			proposition.setModeCreation(EnumModeCreation.CAP);
			proposition.setTypeAvancement(typeAvancement);
			proposition.setVerifie(EnumVerifierAG.NON);
			proposition.setDateEntreeGrade(carriereDTO.getDateEntreeGrade());
			proposition.setDateEntreeEchelon(carriereDTO.getDateEntreeEchelon());
			
			// **** liens
			proposition.setFichePosition(lastFichePositionAdmin);
            proposition.setStatutActuel(carriereDTO.getLastStatut());
			if (EnumTypeAvancementAG.GRADE.equals(typeAvancement)){
				proposition.setMotifAvancement(paramCaOrga.getMotifAvancementAg());
                proposition.setStatutAvancement(carriereDTO.getLastStatut());
            }else if (EnumTypeAvancementAG.RECLASSEMENT.equals(typeAvancement)) {
                proposition.setMotifAvancement(paramCaOrga.getMotifAvancementRe());
                proposition.setStatutAvancement(carriereDTO.getLastStatut());
            } else {
                proposition.setMotifAvancement(paramCaOrga.getMotifAvancementPi());
                proposition.setStatutAvancement(paramCaOrga.getStatutPi());
            }
			proposition.setGradeAncien(carriereDTO.getLastGrade());
			if(!carriereDTO.isCarriereSPV()){
				proposition.setChevronAncien(carriereDTO.getLastChevron());
			}
			// Proposition la plus récente de l'année précedente
			proposition.setPropositionAGAnneePrecedente(
							daoPropositionAG.findPropositonAGAnneePrecedente(dateCalcul, carriereDTO.getCarriere(), carriereDTO.getCodeRegroupement()));
			
			// **** listes
			Set<ConditionDeProposition> listConditionDeProposition= new HashSet<ConditionDeProposition>();
			for(IRuleAvg r: ((RuleAvgOr)rule).getListRules()){		// top rule should be an Or rule
				// first level should be RuleAvgLineRule objects
				RuleAvgLineRule lineRule= (RuleAvgLineRule)r;
				// add ConditionDeProposition
				ConditionDeProposition cdp= new ConditionDeProposition();
				cdp.setDateMini(lineRule.getDateRealized());
				cdp.setConditionRemplie(lineRule.isTrue());
				cdp.setConditionAG(lineRule.getLigne());
				cdp.setPropositionAG(proposition);
				
				cdp.setListDetailCondition(createListDetailCondition(simulation, cdp, new HashSet<DetailCondition>(), lineRule.getRule()));
				
				listConditionDeProposition.add(cdp);
			}
			proposition.setListConditionDeProposition(listConditionDeProposition);
			
			// set type proposition:
			// * simulation
			// * promouvable
			// * non promouvables (dans l'ordre): 
			//		- N-1 pour les promouvables N-1 : celles qui étaient promouvables l'année précédente et non promouvables aujourd'hui
			//		- BLO pour les bloquées : position administrative interdissant l'avancement
			//		- RET pour les retardées : celles qui ont eu une position administrative ayant entrainé un retard dans l'avancement
			//		- FUT pour les futures : n'ayant pas atteint l'ancienneté demandée
			//		- ERR pour les en erreur : celles qui ne peuvent être évaluées car une erreur dans la carrière de l'agent
			if (simulation){
				proposition.setTypeProposition(EnumTypePropositionAG.SIMULATION);
				proposition.setDateRetenue(rule.getDateRealized());
			
			}else if (rule.isTrue()!=null){
				
				if (rule.isTrue()){
	
					// MANTIS 22814 SROM 02/2014 : si tableau de reclassement, on prend en compte toutes les positions admin, même celles qui n'autorise pas AVG
                    if (proposition.getFichePosition() != null 
        					// EMUR 13/01/2016 US SRH_RECLAST Prendre en compte le top "autorise reclassement" dans le calcul des propositions
                    		&& ((!proposition.getFichePosition().getPositionAdmin().getAutoriseAvGra() && !proposition.getTableauAG().getTypeRE())
                    		     || (proposition.getTableauAG().getTypeRE() && (!proposition.getFichePosition().getPositionAdmin().getAutoriseRecla() || !proposition.getStatutAvancement().getAutoriseRecla())))) {
                    		//
                        // la position administrative actuelle de l’agent n'autorise pas l'avancement de grade/reclassement
                        proposition.setTypeProposition(EnumTypePropositionAG.BLOQUEE);
                        proposition.setPromouvable(false);
                    } else {
                        // GIFT IN 614 - STH - 21/04/2010 - Propositions en erreur si agent sans situation à la date de promotion
                        Date dateRealized = rule.getDateRealized();
                        Date dateRetenue = null;
                        if (datePromotionTableau != null && dateRealized != null) {
                            dateRetenue = (dateRealized.after(datePromotionTableau) ? dateRealized : datePromotionTableau);
                        } else {
                            dateRetenue = (dateRealized == null ? datePromotionTableau : dateRealized);
                        }

                        if (dateRetenue == null) {
                            proposition.setPromouvable(false);
                            proposition.setTypeProposition(EnumTypePropositionAG.ERREUR);
                            proposition.setMsgControle("Conditions évaluées positivement mais les dates de promotion du tableau et de réalisation des conditions sont indéterminées");
                        } else if (!agentPresent(carriereDTO, dateRetenue)) {
                            // Agent sans situation à la date de promotion
                            proposition.setPromouvable(false);
                            proposition.setTypeProposition(EnumTypePropositionAG.ERREUR);
                            proposition.setMsgControle("Agent sans situation à la date de promotion");
                        } else {
                            // GROU : reprise du mantis 0037902 : Tableau d’avancement- le calcul des propositions ne tient pas compte de la période saisie lors de la création du tableau
                            if(dateRealized==null || UtilsDate.compareByDateOnly(dateRealized, dateFin) <= 0){
                            	proposition.setTypeProposition(EnumTypePropositionAG.PROMOUVABLE);
                            	proposition.setDateRetenue(dateRetenue);                            	
                            }else{
                            	proposition.setPromouvable(false);
                            	proposition.setTypeProposition(EnumTypePropositionAG.FUTURE);
                            	proposition.setMsgControle("Conditions évaluées positivement en dehors de la période du tableau");
                            }
                        }
                    }
				
				}else if (proposition.getPropositionAGAnneePrecedente()!=null 
						&& EnumTypePropositionAG.PROMOUVABLE.equals(proposition.getPropositionAGAnneePrecedente().getTypeProposition())){
					proposition.setTypeProposition(EnumTypePropositionAG.NMOINS1);
				
				}else if (hasFailedAnciennete(listConditionDeProposition)){
					// calcul retard éventuel
					if (computePropositionRetard(proposition, carriereDTO)>0){
						proposition.setTypeProposition(EnumTypePropositionAG.RETARDEE);
					}else{
						proposition.setTypeProposition(EnumTypePropositionAG.FUTURE);
						proposition.setMsgControle("Ne possède pas l'ancienneté requise");
					}
				}else{
					proposition.setTypeProposition(EnumTypePropositionAG.FUTURE);
					// STH - 26/03/2010 - Faute de conjugaison
                    proposition.setMsgControle("Ne répond pas aux conditions de promotion");
				}
			
			}else{
				proposition.setTypeProposition(EnumTypePropositionAG.ERREUR);
				proposition.setMsgControle(rule.getFailureMessage());
			}
	
			// set conditionDePropositionSelectionnee si une seule condition est vérifiée
			// et si proposition de type promouvable
			boolean first= true;
			if (EnumTypePropositionAG.PROMOUVABLE.equals(proposition.getTypeProposition())){
				for (ConditionDeProposition cdp: listConditionDeProposition)
					if (cdp.getConditionRemplie()!=null && cdp.getConditionRemplie()){
						if (first){
							proposition.setConditionDePropositionSelectionnee(cdp);
							first= false;
						}else
							proposition.setConditionDePropositionSelectionnee(null);
					}
			}
			
			// Calcul infos pour avancement SPV
			if(proposition.getDateRetenue() != null && serviceModuleSMRH.isModuleInstalled(IDaoModuleSMRH.MODULE_SDIS)){
				// check si carriere SPV déjà calculé
				if(carriereDTO.getGradeSPV() == null){
					Carriere carSPP = carriereDTO.getCarriere();
					if(carSPP.isCarriereSPP()){
						// Date de recrutement SPP (1er contrat sur la carrière SPP)
						if(carSPP.getListContrat() != null){
							Date debutSPP = new Date();
							for(Contrat con : carSPP.getListContrat()){
								if(UtilsDate.compareByDateOnly(debutSPP, con.getDateDebut())>0){
									debutSPP = con.getDateDebut();
								}
							}
							carriereDTO.setDebutSPP(debutSPP);
						}
						List<Carriere> listCarriere = daoCarriere.findListCarriereByAgent(carriereDTO.getAgentId());
						if(listCarriere != null){
							Carriere carSPV = null;
							for(Carriere car : listCarriere){
								if(car.isCarriereSPV()){
									carSPV = car;
									break;
								}
							}
							if(carSPV != null){
								//Carsynth SPV actuelle
								CarriereSynthetique carSynthSPV = daoCarriereSynthetique.loadCarriereSynthetiqueCompleteValideAtDateByCarriereId(carSPV.getId(), proposition.getDateRetenue());
								if(carSynthSPV != null && carSynthSPV.getGrade() != null){
									// Grade SPV actuel
									carriereDTO.setGradeSPV(carSynthSPV.getGrade());
									// Date d’entrée dans le grade SPV actuel
									if(carSPV.getListFicheGradeEmploi() != null){
										Date entreeGradeActuel = new Date();
										for(FicheGradeEmploi fiche : carSPV.getListFicheGradeEmploi()){
											if(UtilsDate.compareByDateOnly(entreeGradeActuel, fiche.getDateDebut())>=0 &&
													fiche.getGrade().getId().equals(carSynthSPV.getGrade().getId())){
												entreeGradeActuel = fiche.getDateDebut();
											}
										}
										carriereDTO.setEntreeSPV(entreeGradeActuel);
									}
									// Date de recrutement SPV (1er contrat sur la carrière SPV)
									if(carSPV.getListContrat() != null){
										Date debutSPV = new Date();
										for(Contrat con : carSPV.getListContrat()){
											if(UtilsDate.compareByDateOnly(debutSPV, con.getDateDebut())>=0){
												debutSPV = con.getDateDebut();
											}
										}
										carriereDTO.setDebutSPV(debutSPV);
									}
								}
							}
						}
					}
				}
				// ajout des infos pour l'avancement SPV
				if(carriereDTO.getGradeSPV() != null){
					proposition.setGradeSPVActuel(carriereDTO.getGradeSPV());
					if(paramCaGrade.getGrade() != null && paramCaGrade.getGrade().getGradeSPV() != null){
						Hibernate.initialize(paramCaGrade.getGrade().getGradeSPV());
						proposition.setGradeSPVFutur(paramCaGrade.getGrade().getGradeSPV());
						proposition.setDateEntreeSPVActuel(carriereDTO.getEntreeSPV());
						proposition.setDateSPP(carriereDTO.getDebutSPP());
						proposition.setDateSPV(carriereDTO.getDebutSPV());
					}
				}
				
			}
			
		}catch(Exception e){
			if (simulation){
				proposition= null;
				log.error("Error building proposition for "+ carriereDTO.getCarriere() +"/"+ carriereDTO.getCodeRegroupement() +": "+ e.toString());
			}else{
				// Mantis 36793 : Si une Exception est levée par le calcul, la Proposition est Non promouvable
				proposition.setPromouvable(false);
				proposition.setTypeProposition(EnumTypePropositionAG.ERREUR);
				proposition.setMsgControle(e.getLocalizedMessage());
			}
		}
		return proposition;
	}
	
	private boolean positionNAutorisePasAvancement(EnumTypeAvancementAG typeAvancement, FichePositionAdmin fichePositionAdmin) {
		if (fichePositionAdmin == null || fichePositionAdmin.getPositionAdmin() == null) {
			return false;
		}
		PositionAdmin positionAdmin = fichePositionAdmin.getPositionAdmin();
		// On ne vérifie l'autorisation au niveau de la position que si Avancement de grade ou Position interne
		if (EnumTypeAvancementAG.GRADE.equals(typeAvancement)) {
			if (positionAdmin.getAutoriseAvGra() == null || !positionAdmin.getAutoriseAvGra()) {
				log.info("La position " + positionAdmin.getCode() + " n'autorise pas l'avancement de grade");
				return true;
			}
		} else if (EnumTypeAvancementAG.PROMOTION.equals(typeAvancement)) {
			if (positionAdmin.getAutoriseProInt() == null || !positionAdmin.getAutoriseProInt()) {
				log.info("La position " + positionAdmin.getCode() + " n'autorise pas la promotion interne");
				return true;
			}
		}
		return false;
	}

	/**
     * Renvoie true si il existe une fiche grade valide à la date donnée
     * @param carriereDTO
     * @param dateRetenue
     * @return
     */
    private boolean agentPresent(CarrierePropositionAGDTO carriereDTO, Date dateRetenue) {
        Boolean flagPresent = false;
        if (dateRetenue != null) {
            for (FicheGradeEmploi ficheGrade : carriereDTO.getLstFicheGradeEmploiOrderedByDateDesc()) {
                if (!dateRetenue.before(ficheGrade.getDateDebut()) && (ficheGrade.getDateFin() == null || !dateRetenue.after(ficheGrade.getDateFin()))) {
                    flagPresent = true;
                    break;
                }
            }
        }
        return flagPresent;
    }
    
	/**
	 * Retardé : agent qui répond à minima à condition grade (ou filière ou cadre d’emploi ou catégorie) 
	 * et qui est ou a été sur une position administrative (actuelle ou dans le passé, postérieure au dernier avancement de grade 
	 * (i.e. dont la date de début est postérieure à la date d’entrée dans le grade)) qui a un % d’avancement de grade différent de 100%
	 * @return true 
	 */
	private int computePropositionRetard(PropositionAG proposition, CarrierePropositionAGDTO carriereDTO){
		int retard, retardMax= 0;
		for(ConditionDeProposition cdp: proposition.getListConditionDeProposition()){
			for (DetailCondition dc: cdp.getListDetailCondition()){
				retard= 0;
				if (dc.getListJustificatifRetard()!=null)
					for (JustificatifRetardAG jr: dc.getListJustificatifRetard()) 
						retard += jr.getDuree();

				if (retard>0) retardMax= Math.max(retard, retardMax);
			}
		}
		
		proposition.setDureeRetardAnnee(retardMax / (12*30));
		proposition.setDureeRetardJour(retardMax % (12*30));
		
		return retardMax;
	}
    
	/**
	 * @param conditions
	 * @return true if one of the rules has failed due to anciennete
	 */
	private boolean hasFailedAnciennete(Set<ConditionDeProposition> conditions){

		for(ConditionDeProposition cdp: conditions)
			if (cdp.getConditionRemplie()!=null && !cdp.getConditionRemplie())
				for(DetailCondition dc: cdp.getListDetailCondition())
					if (dc.failedAnciennete) return true;
		
		return false;
	}
    
	/**
	 * 
	 * @param listDetailCondition
	 * @param rule
	 * @return
	 */
	@SuppressWarnings("unchecked")
    private Set<DetailCondition> createListDetailCondition(boolean simulation, ConditionDeProposition cdp, Set<DetailCondition> listDetailCondition, IRuleAvg rule){

		if (rule instanceof AbstractRuleComposite<?>){
			// regles and & or
			for (IRuleAvg r: ((AbstractRuleComposite<IRuleAvg>)rule).getListRules())
				listDetailCondition.addAll(createListDetailCondition(simulation, cdp, listDetailCondition, r));
		}else{
			// regle
			DetailCondition dc= new DetailCondition();
			dc.setConditionDeProposition(cdp);
			// TODO cloner l'objet ValeurFonct.. pour un historique ?
			dc.setValeurFonctionCondition(daoValeurFonctionCondition.findById(rule.getValeurFonctionConditionId(),false));
			dc.setDateDetail(rule.getDateRealized());
			// si c'est une regle d'anciennete:
			if (rule instanceof AbstractRuleAvgAnciennete
				&& ((AbstractRuleAvgAnciennete)rule).getAncienneteDTO()!=null){
				AncienneteDTO anciennete= ((AbstractRuleAvgAnciennete)rule).getAncienneteDTO();
				if (!simulation){
					int nbJours= anciennete.getNbJoursAnciennete();
					// calculs en trentiemes
					dc.setAncienneteAnnee(nbJours / (12 * 30));
					dc.setAncienneteJour(nbJours % (12 * 30));
				}
				// association de la liste de JustificatifRetardAG
				HashSet<JustificatifRetardAG> listJR= new HashSet<JustificatifRetardAG>(anciennete.getListJustificatifRetard().size());
				for(JustificatifRetardAG jr: anciennete.getListJustificatifRetard()) {
					jr.setDetailCondition(dc);
					listJR.add(jr);
				}
				dc.setListJustificatifRetard(listJR);
				// set helper variable
				dc.failedAnciennete= (rule.isTrue()==null ? false : !rule.isTrue());
				
				log.trace("New DetailCondition: anciennete("+dc.getAncienneteAnnee()+"ans "+dc.getAncienneteJour()+"jours) nbJustifs="+dc.getListJustificatifRetard().size());
			} else 				// condition formation SPP : on veut modifier le libellé si la collectivité n'est pas paramétrée pour gérer les formations SPP
				if (//BooleanUtils.isTrue(rule.isTrue()) && 
						rule instanceof RuleAvgFormationSPP
						// libelle "A VERIFIER" si la condition à la formation SDIS est vérifiée mais que le paramétrage n'est pas activé au niveau de la collectivité
						&& BooleanUtils.isFalse(cdp.getPropositionAG().getCarriere().getCollectivite().getSaisirFormationSPP())) {
							cdp.getPropositionAG().setMotifExclusion("A VERIFIER - ");
				}
				


			listDetailCondition.add(dc);
		}
		
		return listDetailCondition;
	}
	
	/**
	 * @param carriere
	 * @param codeRegroupement
	 * @return proposition la plus récente de l'année - 1
	 */
//	@Transactional(propagation=Propagation.SUPPORTS,readOnly=true)
//	public PropositionAG findPropositonAGAnneePrecedente(Carriere carriere, Long codeRegroupement){
//		return daoPropositionAG.findPropositonAGAnneePrecedente(carriere, codeRegroupement);
//	}
		
	/**
	 * @see fr.sedit.grh.coeur.ca.avg.services.IServicePropositionAG#loadCompletePropositonAGById(java.lang.Integer)
	 */
	@Override
	@Transactional(propagation=Propagation.SUPPORTS,readOnly=true)
	public PropositionAG loadCompletePropositonAGById(Integer propositionId, final boolean lock) {
		return this.daoPropositionAG.loadCompletePropositonAGById(propositionId, lock);
	}
		
	
	/**
	 * @see fr.sedit.grh.coeur.ca.avg.services.IServicePropositionAG#countExtraValidPropositionsAG(fr.sedit.grh.coeur.ca.avg.model.PropositionAG)
	 */
	@Override
	@Transactional(propagation=Propagation.SUPPORTS,readOnly=true)
	public Long countExtraValidPropositionsAG(PropositionAG propositionAG) {
		return this.daoPropositionAG.countExtraValidPropositionsAG(propositionAG);
	}

	/**
	 * @see IServicePropositionAG#findListPropositionAGDTO(Map, Map, long, long, String)
	 */
	@Override
	@Transactional(propagation=Propagation.SUPPORTS,readOnly=true)
	public List<PropositionAGDTO> findListPropositionAGDTO(Map<String, Integer> tri, Map<String, Object> criteres, long firstLine, long limitLine, String filter) {
		filtreCriteresSousServices(criteres);
		return daoPropositionAG.findListPropositionAGDTO(tri, criteres, firstLine, limitLine, filter);
	}


	/**
	 * @see IServicePropositionAG#countPropositionWithCriteria(Map, String)
	 */
	@Override
	@Transactional(propagation=Propagation.SUPPORTS,readOnly=true)
	public Long countPropositionWithCriteria(Map<String, Object> criteria, String filter) {
		filtreCriteresSousServices(criteria);
		return daoPropositionAG.countPropositionWithCriteria(criteria, filter);
	}

	/**
	 * @see IServicePropositionAG#changeVerifieePropositionAG(Integer, EnumVerifierAG)
	 */
	@Override
	@Transactional(propagation=Propagation.REQUIRED)
	public void changeVerifieePropositionAG(Integer propositionId, EnumVerifierAG enumVerifier) {
		 daoPropositionAG.changeVerifieePropositionAG(propositionId, enumVerifier);
	}

	/**
	 * @see IServicePropositionAG#exclureReintegrerPropositionAG(Integer, boolean)
	 */
	@Override
	@Transactional(propagation=Propagation.REQUIRED)
	public void exclureReintegrerPropositionAG(Integer propositionId, boolean promouvable) {
		daoPropositionAG.exclureReintegrerPropositionAG(propositionId, promouvable);
		PropositionAG prop = findById(propositionId);
		// MANTIS 14738 SROM 03/2012 : maj du statut et motif d'avancement
		if (promouvable && prop != null 
				&& prop.getMotifAvancement() == null && prop.getStatutAvancement() == null) {
			ParamCaOrga paramCaOrga = daoParamCaOrga.findByOrganisme(prop.getTableauAG().getOrganisme());
	        if (EnumTypeAvancementAG.GRADE.equals(prop.getTypeAvancement())) {
	            prop.setMotifAvancement(paramCaOrga.getMotifAvancementAg());
	            prop.setStatutAvancement(prop.getStatutActuel());
	        } else if (EnumTypeAvancementAG.RECLASSEMENT.equals(prop.getTypeAvancement())) {
	            prop.setMotifAvancement(paramCaOrga.getMotifAvancementRe());
	            prop.setStatutAvancement(prop.getStatutActuel());
	        } else {
	            prop.setMotifAvancement(paramCaOrga.getMotifAvancementPi());
	            prop.setStatutAvancement(paramCaOrga.getStatutPi());
	        }
		}
		savePropositionAG(prop);
	}
	
	/**
	 * @see IServicePropositionAG#findListPropositionNonPromouvableAGDTO(Map, Map, long, long, String)
	 */
	@Override
	@Transactional(propagation=Propagation.SUPPORTS,readOnly=true)
	public List<PropositionNonPromouvableAGDTO> findListPropositionNonPromouvableAGDTO(Map<String, Integer> tri, Map<String, Object> criteres, long firstLine, long limitLine, String filter) {
		return daoPropositionAG.findListPropositionNonPromouvableAGDTO(tri, criteres, firstLine, limitLine, filter);
	}
	
	/**
	 * @see IServicePropositionAG#savePropositionAG(PropositionAG)
	 */
	@Override
	@Transactional(propagation=Propagation.REQUIRED)
	public void savePropositionAG(PropositionAG proposition) {
		daoPropositionAG.save(proposition);
	}
	
	/**
	 * @see IServicePropositionAG#exportPropositionAGForExcel(Map)
	 */
	@Override
	@Transactional(propagation=Propagation.SUPPORTS,readOnly=true)
	public List<PropositionAGAExporterDTO> exportPropositionAGForExcel(Map<String, Object> criteres) {
		Boolean isGpecInstalled = this.serviceModuleSMRH.isModuleGPECInstalled();
	    List<PropositionAGAExporterDTO> lst= daoPropositionAG.exportPropositionAGForExcel(criteres,isGpecInstalled);	    
	    if (lst!=null && lst.size()>0){
            Map<String,ViewHierarchieService> mapService = new HashMap<String, ViewHierarchieService>();
	    	Date dateRefTableau = daoPropositionAG.getDateRefFromTableauAG((Integer)criteres.get("tableauAGId"));
            for (PropositionAGAExporterDTO prop : lst) {
	    		if(prop.getIdService()!=null){
	    			if(!mapService.containsKey(prop.getIdService())){
	    				mapService.put(prop.getIdService(), daoService.findViewHierarchieServiceByIdAndDate(prop.getIdService(), dateRefTableau, dateRefTableau));
	    			}
	    			prop.setVueHierarchieService(mapService.get(prop.getIdService()));
	    		}
	    		if(prop.getDateObtentionExamenProGradeActuel() == null && prop.getCodeGrade() != null){
	    			prop.setDateObtentionExamenProGradeActuel(serviceExamenProAgent.getDateMaxByGrade(prop.getIdGradeActuel(),prop.getIdAgent()));   
	    		}
	    		if(prop.getDateObtentionExamenProGradeCible() == null && prop.getCodeGradeCible() != null){
	    			prop.setDateObtentionExamenProGradeCible(serviceExamenProAgent.getDateMaxByGrade(prop.getIdGradeCible(),prop.getIdAgent()));   
	    		}
	    		if(prop.getDateObtentionConcoursGradeActuel() == null && prop.getCodeGrade() != null){
	    			prop.setDateObtentionConcoursGradeActuel(serviceConcoursAgent.getDateMaxByGrade(prop.getIdGradeActuel(),prop.getIdAgent()));   
	    		}
	    		if(prop.getDateObtentionConcoursGradeCible() == null && prop.getCodeGradeCible() != null){
	    			prop.setDateObtentionConcoursGradeCible(serviceConcoursAgent.getDateMaxByGrade(prop.getIdGradeCible(),prop.getIdAgent()));   
	    		}
                //ESRH-6729 Pour alimentation des nouveaux champs LDG
                String carriereId = prop.getCarriereId();
                Date dateRef = prop.getDateCalcul();
                FicheEvaluation ficheEval = serviceFicheEvaluation.findLastFicheEvaluationCRValideByCarriereAndDate(carriereId,  dateRef);
                if (ficheEval != null) {
                    prop.setLibelleCampagneEvaluation(ficheEval.getCampagneEvaluation().getLibelle());
                    prop.setDateEntretien(ficheEval.getDateEntretien());
                    CompteRenduEntretien cre = (CompteRenduEntretien) ficheEval.getListCompteRendu().toArray()[0];
                    if (cre != null) {
                        prop.setValeurProfessionnelle(cre.getValeurProfessionnelle().getLibelle());
                        prop.setPoidsValeurProfessionnelle(""+cre.getValeurProfessionnelle().getPoids());
                    }
                }
                // Recherche du 1er avis Evaluateur
                List<AvisAGDTO> lstAvisAG = prop.getListAvisAGDTO();
                if (lstAvisAG != null && lstAvisAG.size() > 0) {
                    for (AvisAGDTO avisAG : lstAvisAG) {
                        if ("Evaluateur".equals(avisAG.getQualite())) {
                            prop.setAvisEvaluateur(""+avisAG.getAppreciationLibelle());
                            prop.setCommentaireEvaluateur(""+avisAG.getCommentaire());
                            break;
                        }
                    }
                }
	    		
	    	}
	        Collections.sort(lst, new PropositionAGAExporterDTO.ComparatorByMatriculeCodeRegroupementGradeCible());
	    }
	    return lst;
	}

	/**
	 * @see IServicePropositionAG#calculListDetailAvancementVerificationPropositionAg(Integer)
	 */
	@Override
	@Transactional(propagation=Propagation.SUPPORTS,readOnly=true)
	public List<DetailAvancementAGDTO> calculListDetailAvancementVerificationPropositionAg(Integer tableauId) {
		return daoPropositionAG.calculListDetailAvancementVerificationPropositionAg(tableauId);
	}
	
	/**
	 * @see IServicePropositionAG#changeDateOfPropositionAG(Integer, String, Date)
	 */
	@Override
	@Transactional(propagation=Propagation.REQUIRED)
	public void changeDateOfPropositionAG(Integer propositionId, String nomChamps, Date date) {
		 daoPropositionAG.changeDateOfPropositionAG(propositionId, nomChamps, date);
		
	}
	
	/**
	 * @see IServicePropositionAG#changePromuPropositionAG(Integer, boolean)
	 */
	@Override
	@Transactional(propagation=Propagation.REQUIRED)
	public void changePromuPropositionAG(Integer propositionId, boolean promu) {
		 daoPropositionAG.changePromuPropositionAG(propositionId, promu);
	}
	
    /**
     * @see fr.sedit.grh.coeur.ca.avg.services.IServicePropositionAG#updatePromuForPromouvableAndTableauReclassement(java.lang.Integer)
     */
    @Override
	@Transactional(propagation = Propagation.REQUIRED)
    public void updatePromuForPromouvableAndTableauReclassement(final Integer idTableau) {
        this.daoPropositionAG.updatePromuForPromouvableAndTableauReclassement(idTableau);
    }
    
	/**
	 * @see IServicePropositionAG#countPropositionAGInjection(Map, String)
	 */
	@Override
	@Transactional(propagation=Propagation.SUPPORTS,readOnly=true)
	public Long countPropositionAGInjection(Map<String, Object> criteria, String filter) {
		return daoPropositionAG.countPropositionAGInjection(criteria, filter);
	}
	
	/**
	 * @see IServicePropositionAG#findPropositionAGInjection(Map, Map, long, long, String)
	 */
	@Override
	@Transactional(propagation=Propagation.SUPPORTS,readOnly=true)
	public List<PropositionAGDTO> findPropositionAGInjection(Map<String, Integer> tri, Map<String, Object> criteres, long firstLine, long limitLine, String filter) {
		return daoPropositionAG.findPropositionAGInjection(tri, criteres, firstLine, limitLine, filter);
	}

	/**
	 * @see IServicePropositionAG#exclureProposition(PropositionAG)
	 */
	@Override
	@Transactional(propagation=Propagation.REQUIRED)
	public void exclureProposition(Integer propositionAGId) {
		exclureReintegrerPropositionAG(propositionAGId, false);
	}

	/**
	 * @see IServicePropositionAG#reintegrerPropositionAGNonPromouvable(Integer)
	 */
	@Override
	@Transactional(propagation=Propagation.REQUIRED)
	public void reintegrerPropositionAGNonPromouvable(Integer propositionAGId) {
		exclureReintegrerPropositionAG(propositionAGId, true);
	}

	/**
	 * @see IServicePropositionAG#refuserPropositionAG(Integer, Date, String))
	 */
	@Override
	@Transactional(propagation=Propagation.REQUIRED)
	public void refuserPropositionAG(Integer propositionAGId, Date dateRefus,
			String motifRefus) {
		daoPropositionAG.refuserPromotion(propositionAGId, dateRefus, motifRefus);
	}

	/**
	 * @see IServicePropositionAG#excluePropositionManuellement(Integer, String)
	 */
	@Override
	@Transactional(propagation=Propagation.REQUIRED)
	public void excluePropositionManuellement(Integer propositionId,
			String motif) {
		daoPropositionAG.excluePropositionManuellement(propositionId, motif);
		
	}

	/**
	 * @see IServicePropositionAG#loadByTableauAndAgent(TableauAG, Agent)
	 */
	@Override
	@Transactional(propagation=Propagation.SUPPORTS,readOnly=true)
	public List<PropositionAG> loadByTableauAndAgent(TableauAG tableauAG, Agent agent) {
		return daoPropositionAG.loadByTableauAndAgent(tableauAG, agent);
	}

	/**
	 * @see IServicePropositionAG#loadByTableauAndAgent(TableauAG, Agent)
	 */
	@Override
	@Transactional(propagation=Propagation.SUPPORTS,readOnly=true)
	public List<PropositionAG> loadByListTableauAndCarriere(List<Integer> listTableauAG, Carriere carriere) {
		return daoPropositionAG.loadByListTableauAndCarriere(listTableauAG, carriere);
	}
	
	/**
	 * @see IServicePropositionAG#findListSimulePropositionId(String, Long))
	 */
	@Override
	@Transactional(propagation=Propagation.SUPPORTS,readOnly=true)
	public List<Integer> findListSimulePropositionId(String carriereId,
			Long codeRegroupement) {
		return daoPropositionAG.findListSimulePropositionId(carriereId, codeRegroupement);
	}
	
	@Override
	@Transactional(propagation=Propagation.SUPPORTS,readOnly=true)
	public List<Integer> findListPropositionAGIdsByCollectivitesForControl(List<String> idCollectivites) {
		return daoPropositionAG.findListPropositionAGIdsByCollectivitesForControl(idCollectivites);
	}
	
    /**
     * @see fr.sedit.grh.coeur.ca.avg.services.IServicePropositionAG#updateCarriereSynthetique(fr.sedit.grh.coeur.ca.avg.model.PropositionAG, boolean)
     */
    @Override
	@Transactional(propagation=Propagation.REQUIRED)
    public void updateCarriereSynthetique(PropositionAG proposition, boolean isGpecInstalled) {
        daoCarriereSynthetique.procedureRHMajCarSynth(proposition.getCarriere().getCollectivite().getOrganisme().getId(), proposition.getCarriere().getAgent().getId(), isGpecInstalled);
    }

    /**
     * @see fr.sedit.grh.coeur.ca.avg.services.IServicePropositionAG#updateRegimeAbsence(fr.sedit.grh.coeur.ca.avg.model.PropositionAG)
     */
    @Override
	@Transactional(propagation=Propagation.REQUIRED)
    public void updateRegimeAbsence(PropositionAG proposition) {
        daoRegimeAbsence.procedureRHGenereRegime(1, proposition.getCarriere().getCollectivite().getOrganisme().getId(), proposition.getCarriere().getAgent().getId());
    }

	/**
	 * @see IServicePropositionAG#deleteProposition(PropositionAG)
	 */
	@Override
	@Transactional(propagation=Propagation.REQUIRED)
	public void deleteProposition(PropositionAG propositionAG) {
		daoPropositionAG.delete(propositionAG);
	}

   @Override
    @Transactional(propagation=Propagation.REQUIRED)
    public void deletePropositionById(Integer id) {
        daoPropositionAG.deleteById(id);
    }

	
	
	/**
	 * @see IServicePropositionAG#deletePropositionsByTableau(TableauAG)
	 */
	@Override
	@Transactional(propagation=Propagation.REQUIRED)
	public void deletePropositionsByTableau(TableauAG tableauAG) {
		daoPropositionAG.deletePropositionsByTableau(tableauAG);
	}
	
	/**
	 * @see IServicePropositionAG#deletePropositionsByTableauAGAndGradeCible(TableauAG, Grade)
	 */
	@Override
	@Transactional(propagation=Propagation.REQUIRED)
	public void deletePropositionsByTableauAGAndGradeCible(TableauAG tableauAG,Grade gradeCible) {
		daoPropositionAG.deletePropositionsByTableauAGAndGradeCible(tableauAG, gradeCible);
	}
	
	@Override
    @Transactional(propagation = Propagation.REQUIRED)
    public void deleteAllPropositionsSimulation() {
        this.daoPropositionAG.deleteAllPropositionsSimulation();
    }

	/**
	 * @see IServicePropositionAG#deletePropositionSimule(String, Long)
	 */
	@Override
	@Transactional(propagation=Propagation.REQUIRED)
	public void deletePropositionSimule(String carriereId, Long codeRegroupement) {
		daoPropositionAG.deleteAllPropositionSimule(carriereId, codeRegroupement, null,null,null);
	}

	/**
	 * @see IServicePropositionAG#deleteAllPropositionSimule(List, Grade, Grade, EnumTypeAvancementAG)
	 */
	@Override
	@Transactional(propagation=Propagation.REQUIRED)
	public void deleteAllPropositionSimule(Organisme organisme, Date dateDebut, Date dateFin, 
											Set<Collectivite> listCollectivites, Set<Service> listServices, 
											Set<Categorie> listCategories, Set<CadreEmploi> listCadreEmplois, Set<Filiere> listFilieres, 
											Grade gradeCible, Grade gradeSource, final List<EnumTypeAvancementAG> listTypeAvancement) {
		
		IDaoCursor cursor= daoCarriere.getCursorOnCarriereForPropositionAG(organisme, dateDebut, dateFin, listCollectivites, listCategories, listFilieres, 
																			null, null, listServices, listCadreEmplois);
		CarrierePropositionAGDTO carriereDTO;
		int nbDel= 0;
		
		while (cursor.next()) {
			carriereDTO = (CarrierePropositionAGDTO) cursor.getFirstObject();
			nbDel += daoPropositionAG.deleteAllPropositionSimule(carriereDTO.getId(), carriereDTO.getCodeRegroupement(), gradeCible, gradeSource, listTypeAvancement);
		}
		log.info(nbDel +" propositions supprimées");
	}

	/**
	 * @see IServicePropositionAG#deletePropositionSimuleByAgents(List)
	 */
	@Override
	@Transactional(propagation=Propagation.REQUIRED)
	public void deletePropositionSimuleByAgents(List<Agent> agents) {
		daoPropositionAG.deletePropositionSimuleByAgents(agents);
	}

	/**
	 * @see IServicePropositionAG#loadPropositionWithContent(Integer)
	 */
	@Override
	@Transactional(propagation=Propagation.SUPPORTS,readOnly=true)
	public PropositionAG loadPropositionWithContent(Integer propositionId) {
		return daoPropositionAG.loadPropositionWithContent(propositionId);
	}
	
    /**
     * {@inheritDoc}
     * 
     * @see fr.sedit.grh.coeur.ca.avg.services.IServicePropositionAG#findCollectiviteByPropositionIds(java.util.List)
     */
	@Transactional(propagation = Propagation.SUPPORTS, readOnly = true)
	@Override
	public List<CollectiviteNumerotationArreteParamDTO> findCollectiviteByPropositionIds(final List<Integer> propositionIds) {
		
		if (CollectionUtils.isEmpty(propositionIds)) {
			return new ArrayList<CollectiviteNumerotationArreteParamDTO>();
		} else {
			return this.daoPropositionAG.findCollectiviteByPropositionIds(propositionIds);
		}
	}
	
	/**
	 * @see IServicePropositionAG#findListPropositionByTableauGrade(Grade)
	 */
	@Override
	@Transactional(propagation=Propagation.SUPPORTS,readOnly=true)
	public List<PropositionAG> findListPropositionByTableauGrade(TableauAG tableau, Grade grade){
		return daoPropositionAG.findListPropositionByTableauGrade(tableau, grade);
	}

	/**
	 * @see IServicePropositionAG#countPromotionsConcoursAgent(Map, String)
	 */
	@Override
	@Transactional(propagation=Propagation.REQUIRED)
	public Long countPromotionsConcoursAgent(Map<String, Object> criteria,
			String filter) {
		return daoPropositionAG.countPromotionsConcoursAgent(criteria, filter);
	}

	/**
	 * @see IServicePropositionAG#findListPromotionsConcoursAgent(Map, Map, long, long, String)
	 */
	@Override
	@Transactional(propagation=Propagation.REQUIRED)
	public List<PropositionsConcoursAgentDTO> findListPromotionsConcoursAgent(
			Map<String, Integer> tri, Map<String, Object> criteria,
			long firstLine, long limitLine, String filter) {
		
		List<PropositionsConcoursAgentDTO> listeDTO = null;
		List<PropositionAG> listePropositions = daoPropositionAG.findListPromotionsConcoursAgent(tri, criteria, firstLine, limitLine, filter);
		
		if (listePropositions!=null && !listePropositions.isEmpty()) {
			listeDTO = new ArrayList<PropositionsConcoursAgentDTO>(listePropositions.size());
			
			for (PropositionAG proposition : listePropositions) {
				PropositionsConcoursAgentDTO propDTO = new PropositionsConcoursAgentDTO();
				propDTO.setId(proposition.getId());
				propDTO.setVersion(proposition.getVersion());
				propDTO.setAgent(daoAgent.findAgentStatutDTOById(proposition.getCarriere().getAgent().getId()));
				propDTO.setPromu(proposition.getPromu());
				propDTO.setDateInjection(proposition.getDateInjection());
				propDTO.setDateNomination(proposition.getDateNomination());
				propDTO.setDateObtention(proposition.getReussiteConcours().getDateObtention());
				// echelon cible peut etre null
				propDTO.setChevronCible(proposition.getChevronCible()!=null ? proposition.getChevronCible().getCode() : null);
				propDTO.setGraceCible(proposition.getGradeCible().getLibelleLong());
				propDTO.setLibelleConcours(proposition.getReussiteConcours().getConcours().getLibelle());
				// imprime peut etre null
				propDTO.setNumeroArrete(proposition.getImprime()!=null ? proposition.getImprime().getNumeroArrete() : null);
				propDTO.setTypeConcours(proposition.getReussiteConcours().getTypeConcours().getLibelle());
				listeDTO.add(propDTO);
			}
		}
		return listeDTO;
	}

	/**
	 * @see IServicePropositionAG#officialisePromotion(Integer, Boolean)
	 */
	@Override
	@Transactional(propagation=Propagation.REQUIRED)
	public void officialisePromotion(Integer propositionId, Boolean promu) {
		daoPropositionAG.officialisePromotion(propositionId, promu);
	}

	/**
	 * @see IServicePropositionAG#findFichierImprimeByListPropositionsId(List)
	 */
	@Override
	@Transactional(propagation=Propagation.SUPPORTS,readOnly=true)
	public List<Imprime> findFichierImprimeByListPropositionsId(List<Integer> lstPropositionIds) {
		return daoPropositionAG.findFichierImprimeByListPropositionsId(lstPropositionIds);
	}
	
	@Override
	@Transactional(propagation=Propagation.SUPPORTS,readOnly=true)
    public Imprime findImprimeByPropositionId(Integer idProposition) {
        return daoPropositionAG.findImprimeByPropositionId(idProposition);
    }

	/**
	 * @see IServicePropositionAG#findFichierImprimebyTableauId(Integer)
	 */
	@Override
	@Transactional(propagation=Propagation.SUPPORTS,readOnly=true)
	public List<Imprime> findFichierImprimebyTableauId(Integer tableauId) {
		return daoPropositionAG.findFichierImprimebyTableauId(tableauId);
	}

	
	/**
	 * @see IServicePropositionAG#findListPropositionsOfficialisees4Arretes(TableauAG, List)
	 */
	@Override
	@Transactional(propagation=Propagation.SUPPORTS,readOnly=true)
	public List<Integer> findListPropositionsOfficialisees4Arretes(TableauAG tableau, List<Integer> propositionIds) {
		return daoPropositionAG.findListPropositionsOfficialisees4Arretes(tableau, propositionIds);
	}
	
	/**
     * @see IServicePropositionAG#findInjectionPropIdsWithCriteria(Map)
     */
    @Override
	@Transactional(propagation = Propagation.SUPPORTS, readOnly = true)
    public List<Integer> findInjectionPropIdsWithCriteria(Map<String, Object> propositionCriteria) {
        return daoPropositionAG.findInjectionPropIdsWithCriteria(propositionCriteria);
    }

    /**
     * @param criteres
     */
    private void filtreCriteresSousServices(Map<String, Object> criteres) {
        if(criteres.get("serviceId") instanceof String){
            Boolean sservices = (Boolean) criteres.get("sousServices");
            if (sservices == null) {
            	sservices = Boolean.FALSE;
            }
            String serviceId = (String) criteres.get("serviceId");
            if (serviceId != null && sservices) {
                String idcollectivite = (String) criteres.get("collectiviteId");
                Collectivite collectivite = daoCollectivite.findById(idcollectivite, false);
                List<ServiceDetailDTO> services = serviceServiceDetail.loadArborescenceServiceDetailByCollectivite(collectivite);
                ServiceDetailDTO[] tabservices = new ServiceDetailDTO[services.size()];
                tabservices = services.toArray(tabservices);
                List<String> sousservices = getListSousServices(tabservices, serviceId);
                criteres.put("serviceId", sousservices);
            }
        }
    }
	 
	 /**
	 * @param services
	 * @param serviceId
	 * @return
	 */
	private List<String> getListSousServices(ServiceDetailDTO[] services, String serviceId) {
        List<String> ret = new ArrayList<String>();
        for (int i = 0; i < services.length; i++) {
            ServiceDetailDTO service = services[i];
            if (serviceId == null) {
                ret.add(service.getService().getId());
                if (service.getListServiceCaseFilles().length > 0) {
                    ret.addAll(getListSousServices(service.getListServiceCaseFilles(), null));
                }
            } else if (service.getService().getId().equals(serviceId)) {
                ret.add(service.getService().getId());
                if (service.getListServiceCaseFilles().length > 0) {
                    ret.addAll(getListSousServices(service.getListServiceCaseFilles(), null));
                }
            }
        }
        if (serviceId != null && ret.isEmpty()) {
            for (int i = 0; i < services.length; i++) {
                ServiceDetailDTO service = services[i];
                if (service.getListServiceCaseFilles().length > 0) {
                    ret.addAll(getListSousServices(service.getListServiceCaseFilles(), serviceId));
                }
            }
        }
        return ret;
    }

	 /**
	 * @see fr.sedit.grh.coeur.ca.avg.services.IServicePropositionAG#findListPropositionAG(java.util.Map, java.util.Map)
	 */
	@Override
	@Transactional(propagation=Propagation.REQUIRED)
	public List<PropositionAG> findListPropositionAG(Map<String, Integer> tri, Map<String, Object> criteres) {
		return daoPropositionAG.findListPropositionAG(tri, criteres);
	}
	 
	/**
	 * @see fr.sedit.grh.coeur.ca.avg.services.IServicePropositionAG#findListPorpositionAGByConcoursAgent(java.lang.String)
	 */
    @Override
	@Transactional(propagation = Propagation.SUPPORTS, readOnly=true)
    public List<PropositionAG> findListPorpositionAGByConcoursAgent(String idConcoursAgent) {
		return daoPropositionAG.findListPorpositionAGByConcoursAgent(idConcoursAgent);
	}
     
    @Override
	@Transactional(propagation = Propagation.REQUIRED)
    public void setControleInjectionExecuted(final String codeCollectivite, final String etatTraitement) {
        Notice notice = this.findNoticeForControleInjection(codeCollectivite);
        if (notice == null) {
            notice = new Notice();
            notice.setLigne1("EGRH_AVG_CONTROLE_INJECTION" + codeCollectivite);
        }
        notice.setDateCreat(new Date());
        notice.setLigne2(etatTraitement);
        this.daoNotice.save(notice);
    }

    /**
     * @see fr.sedit.grh.ca.ave.services.IServiceInjection#findNoticeForControleInjection(java.lang.String)
     */
    @Override
	@Transactional(propagation = Propagation.SUPPORTS, readOnly = true)
    public Notice findNoticeForControleInjection(final String codeCollectivite) {
        Notice notice = new Notice();
        notice.setLigne1("EGRH_AVG_CONTROLE_INJECTION" + codeCollectivite);
        List<Notice> l = this.daoNotice.findByExample(notice);
        if (l != null && l.size() > 0) {
            return l.get(0);
        }
        return null;
    }

    @Override
	@Transactional(propagation = Propagation.SUPPORTS, readOnly = true)
    public List<PropositionAG> loadListPropositionByListIdForContexteArrete(List<Integer> lstid) {
        return this.daoPropositionAG.loadListPropositionByListIdForContexteArrete(lstid);
    }
    
    /**
     * @see fr.sedit.grh.coeur.ca.avg.services.IServicePropositionAG#findPropositionAGByIdImprime(java.lang.String)
     */
    @Override
	@Transactional(propagation = Propagation.SUPPORTS, readOnly = true)
    public List<PropositionAG> findPropositionAGByIdImprime(String idImprime) {
    	return daoPropositionAG.findPropositionAGByIdImprime(idImprime);
    }
    
    /**
     * @see fr.sedit.grh.coeur.ca.avg.services.IServicePropositionAG#findInfosAgentByProposition(java.lang.Integer)
     */
    @Override
	@Transactional(propagation = Propagation.SUPPORTS, readOnly = true)
    public AgentDTO findInfosAgentByProposition(Integer idProposition) {
    	return daoPropositionAG.findInfosAgentByProposition(idProposition);
    }
    
    /**
     * @see fr.sedit.grh.coeur.ca.avg.services.IServicePropositionAG#countPropositionsPromouvableByTableauAG(java.lang.String)
     */
    @Override
    @Transactional(propagation = Propagation.SUPPORTS, readOnly = true)
	public Long countPropositionsPromouvableByTableauAG(String code){
    	return daoPropositionAG.countPropositionsPromouvableByTableauAG(code);
    }
    
    /**
     * @see fr.sedit.grh.coeur.ca.avg.services.IServicePropositionAG#orderPropositionAGIdByCollectivite(list<java.lang.Integer>)
     */
    @Override
    @Transactional(propagation = Propagation.SUPPORTS, readOnly = true)
    public List<Integer> orderPropositionAGIdByCollectivite(List<Integer> propositionIds) {
        return daoPropositionAG.orderPropositionAGIdByCollectivite(propositionIds);
    }
    
    /**
	 * @see IServicePropositionAG#changePromuPropositionAG(Integer, boolean)
	 */
	@Override
	@Transactional(propagation=Propagation.REQUIRED)
	public void changePromuSPVPropositionAG(Integer propositionId, boolean promu) {
		 daoPropositionAG.changePromuSPVPropositionAG(propositionId, promu);
	}

    @Override
    public List<Integer> findPropositionAGEditAllImprimeAll(Map<String, Object> criteria) {
        List<PropositionAGDTO> listProposition = daoPropositionAG.findPropositionAGInjection(null, criteria, -1, -1, null);
        List<Integer> propositionIds = new ArrayList<Integer>();
        for (PropositionAGDTO propositionAGDTO : listProposition) {
            propositionIds.add(propositionAGDTO.getId());
        }
        return propositionIds;
    }
    
    
    @Override
    public List<Integer> findListPropositionIdByMatricule(String matricule, String typeavancement, String typePropositon, int tableauId, String codeGrade, String codeGradeCible) {
       return daoPropositionAG.findListPropositionIdByMatricule(matricule, typeavancement, typePropositon, tableauId, codeGrade , codeGradeCible);
    }

    @Override
    public List<Integer> findListPropositionIdByMatricules(List<String> matricules, String typeavancement) {
       return daoPropositionAG.findListPropositionIdByMatricules(matricules, typeavancement);
    }

       @Override
       public void updatePromuClassementDateNominationDateEffetFromIdProposition(Integer idProposition, Date datePromotion, Integer idClassement, boolean promu) {
               daoPropositionAG.updatePromuClassementDateNominationDateEffetFromIdProposition(idProposition, datePromotion ,idClassement, promu);
       }

       @Override
       public Date getDatePromotionFromTableauAG(Integer idTableauAG) {
               return daoPropositionAG.getDatePromotionFromTableauAG(idTableauAG);
       }

       @Override
       public List<Integer> getIdClassementFromValeurSaisieLibre(String saisieLibre) {
               return daoPropositionAG.getIdClassementFromValeurSaisieLibre(saisieLibre);
       }

       @Override
       public Integer insertNouveauRangClassementAg(String saisieLibre) {
               return daoPropositionAG.insertNouveauRangClassementAg(saisieLibre);
       }

       @Override
       public boolean isValeurExisting(String valeur) {
    	   return daoPropositionAG.isValeurExisting(valeur);
       }
       
       @Override
       public List<Integer> getIdClassementFromValeur(String valeur) {
    	   return daoPropositionAG.getIdClassementFromValeur(valeur);
       }
       
       @Override
       public boolean isSaisieLibreExisting(String saisieLibre) {
    	   return daoPropositionAG.isSaisieLibreExisting(saisieLibre);
       }
}

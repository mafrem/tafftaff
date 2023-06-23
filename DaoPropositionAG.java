package fr.sedit.grh.coeur.ca.avg.dao.impl;

import java.math.BigDecimal;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.hibernate.Criteria;
import org.hibernate.HibernateException;
import org.hibernate.LockMode;
import org.hibernate.Query;
import org.hibernate.criterion.CriteriaSpecification;
import org.hibernate.criterion.Order;
import org.hibernate.criterion.Projections;
import org.hibernate.criterion.Restrictions;
import org.hibernate.transform.Transformers;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import fr.bl.shared.grh.coeur.dto.CollectiviteNumerotationArreteParamDTO;
import fr.sedit.core.exception.DaoException;
import fr.sedit.core.exception.DaoException.TypeDaoException;
import fr.sedit.core.exception.TechnicalException;
import fr.sedit.core.hibernate.SortType;
import fr.sedit.core.security.model.dto.HqlSecure;
import fr.sedit.core.tools.UtilsDate;
import fr.sedit.grh.ca.avg.model.dto.AvisAGDTO;
import fr.sedit.grh.ca.avg.model.dto.ConditionRemplieByPropositionDTO;
import fr.sedit.grh.ca.avg.model.dto.DetailAvancementAGDTO;
import fr.sedit.grh.ca.avg.model.dto.PropositionAGAExporterDTO;
import fr.sedit.grh.ca.avg.model.dto.PropositionAGDTO;
import fr.sedit.grh.ca.avg.model.dto.PropositionDetailsAGDTO;
import fr.sedit.grh.ca.avg.model.dto.PropositionNonPromouvableAGDTO;
import fr.sedit.grh.ca.par.model.enums.EnumModeCreation;
import fr.sedit.grh.coeur.ca.avg.dao.IDaoPropositionAG;
import fr.sedit.grh.coeur.ca.avg.model.ConditionDeProposition;
import fr.sedit.grh.coeur.ca.avg.model.PropositionAG;
import fr.sedit.grh.coeur.ca.avg.model.RangClassementAG;
import fr.sedit.grh.coeur.ca.avg.model.TableauAG;
import fr.sedit.grh.coeur.ca.avg.model.enums.EnumPhaseTableauAG;
import fr.sedit.grh.coeur.ca.avg.model.enums.EnumTypeAvancementAG;
import fr.sedit.grh.coeur.ca.avg.model.enums.EnumTypeInjectionAG;
import fr.sedit.grh.coeur.ca.avg.model.enums.EnumTypePropositionAG;
import fr.sedit.grh.coeur.ca.avg.model.enums.EnumVerifierAG;
import fr.sedit.grh.coeur.ca.par.model.LigneConditionAG;
import fr.sedit.grh.coeur.ca.par.model.enums.EnumDecisionCAP;
import fr.sedit.grh.coeur.cs.model.Agent;
import fr.sedit.grh.coeur.cs.model.Carriere;
import fr.sedit.grh.coeur.cs.model.CarriereSynthetique;
import fr.sedit.grh.coeur.cs.model.Grade;
import fr.sedit.grh.coeur.cs.model.Imprime;
import fr.sedit.grh.coeur.cs.model.dto.AgentDTO;
import fr.sedit.grh.coeur.cs.model.enums.EnumModuleCode;
import fr.sedit.grh.common.db.Transformers4Enum;
import fr.sedit.grh.common.security.SecurityMgrGRH;
import fr.sedit.sedit.hibernate.GenericHibernateDAO;

/**
 * Generated Implementation
 */
@Transactional
public class DaoPropositionAG extends GenericHibernateDAO<PropositionAG, Integer> implements IDaoPropositionAG {

    private static final Log log = LogFactory.getLog(DaoPropositionAG.class);

    static private final String _AND = " and ";

    private final String fromPropoposition = " from PropositionAG as prop "
            + " inner join prop.carriere as car "
            + " inner join car.agent as agent "
            + " inner join prop.statutActuel as statut "
            + " inner join prop.gradeAncien as grade "
            + " left join prop.chevronAncien as chevAnc "
            + " left join car.listFicheAffectation as affect with (affect.dateDebut <= :date and (affect.dateFin is NULL or affect.dateFin >= :date)) "
            + " left join affect.service as service " 
            + " left join prop.rangClassementAG as rang "
            + " left join prop.chevronCible as chevronCible " 
            + " left join prop.motifAvancement as motifAvancement "
            + " left join prop.statutAvancement as statutAvancement ";
    
    // Mantis 5590: pour les agents en erreurs statutActuel et gradeAncien peuvent être null
    private final String fromPropopositionAgentEnErreur = " from PropositionAG as prop "
        + " inner join prop.carriere as car "
        + " inner join car.agent as agent "
        + " left join prop.statutActuel as statut "
        + " left join prop.gradeAncien as grade "
        + " left join prop.chevronAncien as chevAnc "
        + " left join car.listFicheAffectation as affect with (affect.dateDebut <= :date and (affect.dateFin is NULL or affect.dateFin >= :date)) "
        + " left join affect.service as service " 
        + " left join prop.rangClassementAG as rang "
        + " left join prop.chevronCible as chevronCible " 
        + " left join prop.motifAvancement as motifAvancement "
        + " left join prop.statutAvancement as statutAvancement ";

    /** requete des propositions suite aux réussites à concours * */
    private final String fromPropopositionWithConcours = " from PropositionAG as prop " 
            + " inner join prop.carriere as car "
            + " inner join car.agent as agent " 
            + " inner join prop.gradeCible as grade "
            + " left join prop.chevronCible as chevronCible " 
            + " inner join prop.reussiteConcours concoursAgent"
            + " inner join concoursAgent.concours" 
            + " left join prop.imprime";

    private final String requetePropositionAGDTO = "select prop.id as id, prop.version as version,"
            + " prop.verifie as verifiee, " 
            + " agent.id as idAgent, " 
            + " agent.nom as nomAgent,"
            + " agent.prenom as prenomAgent," 
            + " agent.matricule as matriculeAgent,"
            + " service.libelle as libelleService," 
            + " statut.libelle as libelleStatut,"
            + " grade.libelleMoyen as libelleGrade," 
            + " chevAnc.code as codeChevronAncien,"
            + " prop.gradeCible.libelleMoyen as libelleGradeCible," 
            + " prop.promu as promu, "
            + " prop.refuse as refusee, " 
            + " prop.msgControle as msgControle, " 
            + " prop.injection as injection, "
            + " prop.dateRetenue as dateRetenue, " 
            + " prop.dateEffet as dateEffet, "
            + " prop.dateNomination as dateNomination, " 
            + " prop.reliquat as reliquat, "
            + " prop.dateEntreeEchelon as dateEntreeEchelon, " 
            + " prop.typeProposition as typeProposition,  "
            + " prop.typeAvancement as typeAvancement, " 
            + " rang.saisieLibre as saisieLibreRangClassementAG, "
            + " rang.valeur as valeurRangClassementAG, " 
            + " chevronCible.code as codeChevronCible, "
            + " motifAvancement.libelle as libelleMotifAvancement, "
            + " statutAvancement.libelle as libelleStatutAvancement, "
            + " prop.promuSPV as promuSPV, "
            + " prop.dateSPP as dateSPP, "
            + " prop.dateSPV as dateSPV, "
            + " prop.dateEntreeSPVActuel as dateGradeSPV ";

    private static final String countAutreTableau = "select count(*) "
            + " from PropositionAG origine, PropositionAG autrePropoAG , PropositionAE autrePropoAE "
            + " where (origine.tableauAG <> autrePropoAG.tableauAG " 
            + " and origine.carriere = autrePropoAG.carriere "
            + " and origine.codeRegroupement = autrePropoAG.codeRegroupement "
            + " and autrePropoAG.promouvable = true " 
            + " and origine.id = :propid) "
            + " or(origine.carriere = autrePropoAE.carriere "
            + " and origine.codeRegroupement = autrePropoAE.regroupement " 
            + " and autrePropoAE.promouvable = true "
            + " and origine.id = :propid) ";

    /**
     * @see IDaoPropositionAG#findListPropositionAGDTO(Map, Map, long, long, String)
     */
    @Override
	@SuppressWarnings("unchecked")
    @Transactional(propagation = Propagation.SUPPORTS, readOnly = true)
    public List<PropositionAGDTO> findListPropositionAGDTO(Map<String, Integer> tri, Map<String, Object> criteres,
            long firstLine, long limitLine, String filter) {
        try {
            Query query = prepareQueryListPropositions(tri, criteres, requetePropositionAGDTO + fromPropoposition
                    + ", HistoGrade as histoGrade" + ", HistoCadreEmploi as histoCadre "
                    + " where histoGrade.grade = prop.gradeCible" + " and histoGrade.dateDebut <= :date"
                    + " and (histoGrade.dateFin is null or histoGrade.dateFin > :date)" 
                    + " and histoCadre.cadreEmploi = histoGrade.cadreEmploi" 
                    + " and histoCadre.dateDebut <= :date"
                    + " and (histoCadre.dateFin is null or histoCadre.dateFin > :date) ", null, true);
            if (query == null) {
                return new ArrayList<PropositionAGDTO>(0);
            }
            this.applyLimits(filter, query, (int)firstLine, (int)limitLine);
            query.setResultTransformer(Transformers.aliasToBean(PropositionAGDTO.class));

            List<PropositionAGDTO> listResult = ( List<PropositionAGDTO>) this.filterList(filter, query.list());
            int size = listResult.size();
            for (int i = 0; i < size; i++) {
                PropositionAGDTO propositionAGDTO = listResult.get(i);

                // ***************** renseign des conditions s'il y en a un lier a la proposition
                List<ConditionRemplieByPropositionDTO> listConditionRemplie = findListConditionRemplieByPropositionDTO(propositionAGDTO.getId());
                propositionAGDTO.setListConditionAGRemplie(listConditionRemplie);

                // ***************** renseigne les avis **********************************
                List<AvisAGDTO> listAvis = findListAvisAGDTOByPropositionId(propositionAGDTO.getId());
                propositionAGDTO.setListAvisAGDTO(listAvis);

                // ***************** pour les promouvable renseigne autreTableau et date depart
                Boolean promouvable = (Boolean) criteres.get("promouvable");
                if (promouvable != null && promouvable.booleanValue() == true) {
                    // ******************** renseigne dateDepart ***************************
                    Query queryDateDepart = getSession().createQuery(
                            "select max( fiche.dateFin ) " + "from PropositionAG prop, FicheArriveeDepart as fiche "
                                    + "where prop.carriere.collectivite = fiche.collectivite "
                                    + "and prop.carriere.agent = fiche.agent " 
                                    + "and prop.id = :propid");

                    queryDateDepart.setInteger("propid", propositionAGDTO.getId());
                    Date dateDepart = (Date) queryDateDepart.uniqueResult();
                    propositionAGDTO.setDateDepart(dateDepart);

                    // ************* Renseigne le champ autreTableau*************************************
                    Query querycount = getSession().createQuery(countAutreTableau);

                    querycount.setInteger("propid", propositionAGDTO.getId());
                    Long count = (Long) querycount.uniqueResult();
                    if (count.intValue() == 0)
                        propositionAGDTO.setAutreTableau(Boolean.FALSE);
                    else
                        propositionAGDTO.setAutreTableau(Boolean.TRUE);
                    
                    //***** Libelle grade SPV ***/
                    if(propositionAGDTO.getDateGradeSPV() != null){
                    	Query queryGradeSPV  = getSession().createQuery("SELECT prop.gradeSPVActuel, prop.gradeSPVFutur, prop.gradeAncien FROM PropositionAG prop WHERE prop.id = :idProp");
                    	queryGradeSPV.setInteger("idProp", propositionAGDTO.getId());
                    	Object[] result = (Object[])queryGradeSPV.uniqueResult();
                    	if(result.length == 3){
                    		if(result[0] != null){
                    			Grade gradeSPV = (Grade)result[0];
                    			propositionAGDTO.setLibelleGradeSPV(gradeSPV.getLibelleMoyen());
                        		if(result[2]!=null){
                        			Grade gradeSPVEquivalent = ((Grade)result[2]).getGradeSPV();
                        			if(gradeSPVEquivalent != null){
                        				propositionAGDTO.setGradeSPVEquivalent(gradeSPV.getId().equals(gradeSPVEquivalent.getId()));
                        			}
                        		}
                    		}
                    		if(result[1]!=null){
                    			propositionAGDTO.setLibelleGradeSPVFutur(((Grade)result[1]).getLibelleMoyen());
                    		}
                    	}
                    }
                    
                    //***** Type de carrière ***/
                    Query queryTypCar  = getSession().createQuery(
                    		"SELECT libcar.type, coll.saisirFormationSPP " +
                    		"FROM PropositionAG prop " +
                    		"LEFT JOIN prop.carriere car " +
                    		"LEFT JOIN car.libelleCarriere libcar " +
                    		"LEFT JOIN car.collectivite coll " +
                    		"WHERE prop.id = :idProp");
                    queryTypCar.setInteger("idProp", propositionAGDTO.getId());
                    Object[] result = (Object[]) queryTypCar.uniqueResult();
                	if(result != null && "V".equals(result[0])){
                		propositionAGDTO.setCarriereSPV(true);
                	}
                	propositionAGDTO.setSaisieCollectFormationSPP((Boolean)result[1]);
                }
            }
            return listResult;
        } catch (HibernateException hibEx) {
            throw new DaoException(hibEx);
        }
    }

    /**
     * 
     * @param tri
     *            Pas gere
     * @param criteria
     *            Critères pris en compte : tableauAG, gradeID, date
     * @param hql
     * @param finalHql
     * @return
     */
    @SuppressWarnings("unchecked")
    private Query prepareQueryListPropositions(Map<String, Integer> tri, Map<String, Object> criteria, String hql,
            String finalHql, boolean withDateParameter) {
        try {
            Query query = null;
            StringBuffer hqlBuffer = new StringBuffer(hql);

            if (criteria == null)
                return null;

            String where_and = hql.indexOf("where") > -1 ? _AND : " where ";

            TableauAG tableauAG = (TableauAG) criteria.get("tableauAG");

            // if (tableauAG == null) return null;
            // else{
            if (tableauAG != null) {
                hqlBuffer.append(where_and).append(" prop.tableauAG = :tableauAG ");
                where_and = _AND;
            }

            String gradeId = (String) criteria.get("gradeId");
            if (gradeId != null) {
                hqlBuffer.append(where_and).append(" prop.gradeCible.id = :gradeId ");
                where_and = _AND;
            }

            String filiereId = (String) criteria.get("filiereId");
            if (filiereId != null) {
                hqlBuffer.append(where_and).append("histoCadre.filiere.id = :filiereId");
                where_and = _AND;
            }

            String cadreEmploiId = (String) criteria.get("cadreEmploiId");
            if (cadreEmploiId != null) {
                hqlBuffer.append(where_and).append("histoCadre.cadreEmploi.id = :cadreEmploiId");
                where_and = _AND;
            }

            Boolean promouvable = (Boolean) criteria.get("promouvable");
            if (promouvable != null) {
                hqlBuffer.append(where_and).append(" prop.promouvable = :promouvable ");
                where_and = _AND;
            }

            Boolean promu = (Boolean) criteria.get("promu");
            if (promu != null) {
                hqlBuffer.append(where_and).append(" prop.promu = :promu");
                where_and = _AND;
            }

            EnumTypePropositionAG enumTypePropositionAG = (EnumTypePropositionAG) criteria.get("enumTypePropositionAG");
            if (enumTypePropositionAG != null) {
                hqlBuffer.append(where_and).append(" prop.typeProposition =  :enumTypePropositionAG");
                where_and = _AND;
            }

            EnumVerifierAG enumVerifier = (EnumVerifierAG) criteria.get("enumVerifier");
            if (enumVerifier != null) {
                hqlBuffer.append(where_and).append(" prop.verifie = :enumVerifier ");
                where_and = _AND;
            }

            Date dateNominationDebut = (Date) criteria.get("dateNominationDebut");
            if (dateNominationDebut != null) {
                hqlBuffer.append(where_and).append(" prop.dateNomination >=  :dateNominationDebut");
                where_and = _AND;
            }

            Date dateNominationFin = (Date) criteria.get("dateNominationFin");
            if (dateNominationFin != null) {
                hqlBuffer.append(where_and).append(" prop.dateNomination <=  :dateNominationFin");
                where_and = _AND;
            }

            String collectiviteId = (String) criteria.get("collectiviteId");
            if (collectiviteId != null) {
                hqlBuffer.append(where_and).append("car.collectivite.id = :collectiviteId");
                where_and = _AND;
            }

            // GIFT 79012 - STH - 12/08/2009 - Filtrer les propositions par mois d'avancement
            String moisAvancement = (String) criteria.get("moisAvancement");
            if (moisAvancement != null && !"00".equals(moisAvancement)) {
                hqlBuffer.append(where_and).append("MONTH(prop.dateNomination) = :moisAvancement");
                where_and = _AND;
            }
            
            // GIFT 87433 - LDEC - 15/03/2010 - Pour les propositions injectés ne pas afficher les propositions des tabeaux cloturés
            if (criteria.containsKey("hidePropositionOfTableauCloture")) {
            	//  MANTIS 16189 SROM 09/2012 : il faut pouvoir afficher les propositions qui ne viennent pas de tableaux (concours par exemple) 
            	hqlBuffer.append(where_and).append(" (tableau.etatTableau != " + EnumPhaseTableauAG.TABLEAU_CLOS.getCode() + " or tableau is null) ");
                where_and = _AND;
            }

            String agentId = (String) criteria.get("agentId");
            if (agentId != null) {
                hqlBuffer.append(where_and).append(" agent.id = :agentId ");
                where_and = _AND;
            }

            List<String> agentIds = (List<String>) criteria.get("agentIds");
            if (agentIds != null) {
                hqlBuffer.append(where_and).append(" prop.carriere.agent.id in (:agentIds)");
                where_and = _AND;
            }

            String organismeId = (String) criteria.get("organismeId");
            if (organismeId != null) {
                hqlBuffer.append(where_and).append(" agent.organisme.id = :organismeId ");
                where_and = _AND;
                if (collectiviteId != null) {
                    hqlBuffer.append(where_and).append(" car.collectivite.organisme.id = :organismeId ");
                    where_and = _AND;
                }
            }

            Object serviceId = criteria.get("serviceId");
            if (serviceId != null) {
                if (serviceId instanceof List) {
                    hqlBuffer.append(where_and).append("service.id in (:serviceId)");
                } else {
                    hqlBuffer.append(where_and).append("service.id = :serviceId");
                }
                where_and = _AND;
            }

            EnumTypeInjectionAG injection = (EnumTypeInjectionAG) criteria.get("injection");
            if (injection != null) {
                // modif clf 21/04/2008 injection du paramètre par setString
                // hqlBuffer.append(where_and).append("(prop.injection = '" + injection.getCode() + "')");
                hqlBuffer.append(where_and).append(" prop.injection = :injection");
                where_and = _AND;
            }

            Date dateInjection = (Date) criteria.get("dateInjection");
            if (criteria.containsKey("dateInjection") && dateInjection == null) {
                hqlBuffer.append(where_and).append(" prop.dateInjection is null");
                where_and = _AND;
            } else if (criteria.containsKey("dateInjection") && dateInjection != null) {
                hqlBuffer.append(where_and).append(" prop.dateInjection =:dateInjection");
                where_and = _AND;
            }

            Boolean isControlled = (Boolean) criteria.get("isControlled");
            if (isControlled != null) {
                if (isControlled.booleanValue()) {
                    hqlBuffer.append(where_and).append(
                            "(prop.injection = '" + EnumTypeInjectionAG.CONTROLEE_OK.getCode()
                                    + "' OR prop.injection = '" + EnumTypeInjectionAG.CONTROLEE_KO.getCode() + "')");
                } else {
                    hqlBuffer.append(where_and).append(
                            "(prop.injection is null OR (prop.injection != '"
                                    + EnumTypeInjectionAG.CONTROLEE_OK.getCode() + "' AND prop.injection != '"
                                    + EnumTypeInjectionAG.CONTROLEE_KO.getCode() + "'))");
                }
                where_and = _AND;
            }

            Boolean isInjectable = (Boolean) criteria.get("isInjectable");
            if (isInjectable != null) {
                if (isInjectable.booleanValue()) {
                    hqlBuffer.append(where_and)
                            .append(
                                    "( prop.dateNomination is not null " +
                                    "AND ((car.collectivite.periodePaie is not null " +
                                    "AND length(trim(car.collectivite.periodePaie))>0 " +
                                    "AND prop.dateNomination < add_months(to_date(car.collectivite.periodePaie, 'YYYY.MM'),1)) or (car.libelleCarriere.type = 'V')) )");
                } else {
                    hqlBuffer.append(where_and)
                            .append(
                                    "( prop.dateNomination is null " +
                                    "OR ((car.collectivite.periodePaie is null " +
                                    "OR length(trim(car.collectivite.periodePaie))=0 " +
                                    "OR prop.dateNomination >= add_months(to_date(car.collectivite.periodePaie, 'YYYY.MM'),1)) and (car.libelleCarriere.type <> 'V') ))");
                }
                where_and = _AND;
            }

            Date dateDebutObtention = (Date) criteria.get("dateDebutObtention");
            if (dateDebutObtention != null) {
                hqlBuffer.append(where_and).append(" concoursAgent.dateObtention >=  :dateDebutObtention");
                where_and = _AND;
            }

            Date dateFinObtention = (Date) criteria.get("dateFinObtention");
            if (dateFinObtention != null) {
                hqlBuffer.append(where_and).append(" concoursAgent.dateObtention <=  :dateFinObtention");
                where_and = _AND;
            }

            EnumDecisionCAP decision = (EnumDecisionCAP) criteria.get("EnumDecisionCAP");
            if (decision != null) {
                hqlBuffer.append(where_and).append(" prop.decision =  :decision");
                where_and = _AND;
            }

            Boolean imprime = criteria.containsKey("imprime");
            if (imprime) {
                hqlBuffer.append(where_and).append(" prop.imprime is null");
                where_and = _AND;
            }
            
            Boolean doublecarriere = criteria.containsKey("doublecarriere");
            if (doublecarriere) {
                hqlBuffer.append(where_and).append(" (prop.dateSPP is not null and prop.dateSPV is not null)");
                where_and = _AND;
            }
            
            

            // Confidentialité
            HqlSecure hqls = getSecurityFilter().secureClause(SecurityMgrGRH.CONFIDENTIALITE_AGENT, "agent");
            if (hqls != null && hqls.where.length() > 0) {
                hqlBuffer.append(where_and).append(hqls.where);
                where_and = _AND;
            }

            Date date = (Date) criteria.get("date");
            if (date == null) {
            	// MANTIS 35381 SROM 01/2017 : on borne la date de réference aux dates du tableau s'il existe
            	date = getDateRefFromTableauAG(tableauAG != null ? tableauAG.getId() : null);
            }
            if (finalHql != null)
                hqlBuffer.append(finalHql);

            StringBuffer orderBy = new StringBuffer();
            
            if (doublecarriere) {
            	orderBy.append(" prop.promu desc")
            		.append(", case when (prop.gradeCible.gradeSPV = prop.gradeSPVActuel) then 1 else 0 end ")
            		.append(", agent.nom, agent.prenom, agent.matricule");
            }

            if (tri != null) {

                if (tri.containsKey("agent")) {
                    String typeTri = (tri.get("agent").equals(SortType.DESCENDING) ? "desc" : "asc");
                    if (orderBy.length() > 0)
                        orderBy.append(", ");
                    orderBy.append(" agent.nom ").append(typeTri);
                    orderBy.append(" ,agent.prenom ").append(typeTri);
                    orderBy.append(" ,agent.matricule ").append(typeTri);
                }
                if (tri.containsKey("dateRetenue")) {
                    String typeTri = (tri.get("dateRetenue").equals(SortType.DESCENDING) ? "desc" : "asc");
                    if (orderBy.length() > 0)
                        orderBy.append(", ");
                    orderBy.append(" prop.dateRetenue ").append(typeTri);
                }
                if (tri.containsKey("dateNomination")) {
                    String typeTri = (tri.get("dateNomination").equals(SortType.DESCENDING) ? "desc" : "asc");
                    if (orderBy.length() > 0)
                        orderBy.append(", ");
                    orderBy.append(" prop.dateNomination ").append(typeTri);
                }
                if (tri.containsKey("dateEffet")) {
                    String typeTri = (tri.get("dateEffet").equals(SortType.DESCENDING) ? "desc" : "asc");
                    if (orderBy.length() > 0)
                        orderBy.append(", ");
                    orderBy.append(" prop.dateEffet ").append(typeTri);
                }
                if (tri.containsKey("dateInjection")) {
                    String typeTri = (tri.get("dateInjection").equals(SortType.DESCENDING) ? "desc" : "asc");
                    if (orderBy.length() > 0)
                        orderBy.append(", ");
                    orderBy.append(" prop.dateInjection ").append(typeTri);
                }
                if (tri.containsKey("refuse")) {
                    String typeTri = (tri.get("refuse").equals(SortType.DESCENDING) ? "desc" : "asc");
                    if (orderBy.length() > 0)
                        orderBy.append(", ");
                    orderBy.append(" prop.refuse ").append(typeTri);
                }
                if (tri.containsKey("msgControle")) {
                    String typeTri = (tri.get("msgControle").equals(SortType.DESCENDING) ? "desc" : "asc");
                    if (orderBy.length() > 0)
                        orderBy.append(", ");
                    orderBy.append(" prop.msgControle ").append(typeTri);
                }
                if (tri.containsKey("retard")) {
                    String typeTri = (tri.get("retard").equals(SortType.DESCENDING) ? "desc" : "asc");
                    if (orderBy.length() > 0)
                        orderBy.append(", ");
                    orderBy.append(" prop.dureeRetardAnnee, prop.dureeRetardJour ").append(typeTri);
                }
                if (tri.containsKey("motifExclusion")) {
                    String typeTri = (tri.get("motifExclusion").equals(SortType.DESCENDING) ? "desc" : "asc");
                    if (orderBy.length() > 0)
                        orderBy.append(", ");
                    orderBy.append(" prop.motifExclusion ").append(typeTri);
                }
                if (tri.containsKey("motifRefus")) {
                    String typeTri = (tri.get("motifRefus").equals(SortType.DESCENDING) ? "desc" : "asc");
                    if (orderBy.length() > 0)
                        orderBy.append(", ");
                    orderBy.append(" prop.motifRefus ").append(typeTri);
                }
                if (tri.containsKey("motifBlocage")) {
                    String typeTri = (tri.get("motifBlocage").equals(SortType.DESCENDING) ? "desc" : "asc");
                    if (orderBy.length() > 0)
                        orderBy.append(", ");
                    orderBy.append(" positionAdmin.libelle ").append(typeTri);
                }
                if (tri.containsKey("codeChevronCible")) {
                    String typeTri = (tri.get("codeChevronCible").equals(SortType.DESCENDING) ? "desc" : "asc");
                    if (orderBy.length() > 0)
                        orderBy.append(", ");
                    orderBy.append(" prop.chevronCible.code ").append(typeTri);
                }
                if (tri.containsKey("libelleGradeCible")) {
                    String typeTri = (tri.get("libelleGradeCible").equals(SortType.DESCENDING) ? "desc" : "asc");
                    if (orderBy.length() > 0)
                        orderBy.append(", ");
                    orderBy.append(" prop.gradeCible.libelleMoyen ").append(typeTri);
                }
                if (tri.containsKey("libelleMotifAvancement")) {
                    String typeTri = (tri.get("libelleMotifAvancement").equals(SortType.DESCENDING) ? "desc" : "asc");
                    if (orderBy.length() > 0)
                        orderBy.append(", ");
                    orderBy.append(" prop.motifAvancement.libelle ").append(typeTri);
                }
                if (tri.containsKey("libelleStatutAvancement")) {
                    String typeTri = (tri.get("libelleStatutAvancement").equals(SortType.DESCENDING) ? "desc" : "asc");
                    if (orderBy.length() > 0)
                        orderBy.append(", ");
                    orderBy.append(" prop.statutAvancement.libelle ").append(typeTri);
                }
                // GIFT 92625 - LDEC - 23 sept. 2010 - Tri par classement priorité au valeur si renseigné sinon c'est par saisie libre 
                if (tri.containsKey("classement")) {
                    String typeTri = (tri.get("classement").equals(SortType.DESCENDING) ? "desc" : "asc");
                    if (orderBy.length() > 0)
                        orderBy.append(", ");
                    orderBy.append(" rang.valeur ").append(typeTri).append(", rang.saisieLibre ").append(typeTri);
                }
            }
            if (orderBy.length() > 0)
            	hqlBuffer.append(" order by ").append(orderBy);

            query = getSession().createQuery(hqlBuffer.toString());

            if (serviceId != null) {
                if (serviceId instanceof List) {
                    query.setParameterList("serviceId", (List) serviceId);
                } else {
                    query.setString("serviceId", (String) serviceId);
                }
            }
            if (tableauAG != null)
                query.setEntity("tableauAG", tableauAG);
            if (gradeId != null)
                query.setString("gradeId", gradeId);
            if (promouvable != null)
                query.setBoolean("promouvable", promouvable.booleanValue());
            if (promu != null)
                query.setBoolean("promu", promu);
            if (enumTypePropositionAG != null)
                query.setParameter("enumTypePropositionAG", enumTypePropositionAG);
            if (enumVerifier != null)
                query.setParameter("enumVerifier", enumVerifier);
            if (withDateParameter)
                query.setDate("date", date);
            if (agentId != null)
                query.setString("agentId", agentId);
            if (dateNominationDebut != null)
                query.setDate("dateNominationDebut", dateNominationDebut);
            if (dateNominationFin != null)
                query.setDate("dateNominationFin", dateNominationFin);
            if (collectiviteId != null)
                query.setString("collectiviteId", collectiviteId);
            if (moisAvancement != null && !"00".equals(moisAvancement)) {
                query.setString("moisAvancement", moisAvancement);
            }
            if (organismeId != null)
                query.setString("organismeId", organismeId);
            if (cadreEmploiId != null)
                query.setString("cadreEmploiId", cadreEmploiId);
            if (filiereId != null)
                query.setString("filiereId", filiereId);
            if (agentIds != null)
                query.setParameterList("agentIds", agentIds);
            if (decision != null)
                query.setParameter("decision", decision);
            if (injection != null)
                query.setString("injection", injection.getCode());
            // filtre sur concours
            if (dateDebutObtention != null)
                query.setDate("dateDebutObtention", dateDebutObtention);
            if (dateFinObtention != null)
                query.setDate("dateFinObtention", dateFinObtention);
            if (dateInjection != null)
                query.setDate("dateInjection", dateInjection);

            if (hqls != null && hqls.criteriaValues != null) {
                query.setProperties(hqls.criteriaValues);
            }

            return query;
        } catch (HibernateException hibEx) {
            throw new DaoException(hibEx);
        }
    }

    /**
     * @see IDaoPropositionAG#countPropositionWithCriteria(Map,String)
     */
    @Override
	@Transactional(propagation = Propagation.SUPPORTS, readOnly = true)
    public Long countPropositionWithCriteria(Map<String, Object> criteria, String filter) {
        try {
            Query query = prepareQueryListPropositions(
                    null,
                    criteria,
                    "select count(*) "
                            +
                            // fromPropoposition+
                            " from PropositionAG as prop inner join prop.gradeCible as grade inner join prop.carriere as car inner join car.agent as agent "
                            + " left join car.listFicheAffectation as affect with (affect.dateDebut <= :date and (affect.dateFin is NULL or affect.dateFin >= :date)) "
                            + " left join affect.service as service " + ", HistoGrade as histoGrade"
                            + ", HistoCadreEmploi as histoCadre " + " where histoGrade.grade = grade"
                            + " and histoGrade.dateDebut <= :date"
                            + " and (histoGrade.dateFin is null or histoGrade.dateFin > :date)"
                            + " and histoCadre.cadreEmploi = histoGrade.cadreEmploi"
                            + " and histoCadre.dateDebut <= :date"
                            + " and (histoCadre.dateFin is null or histoCadre.dateFin > :date) ", null, true);
            return (Long) query.uniqueResult();
        } catch (HibernateException hibEx) {
            throw new DaoException(hibEx);
        }
    }

    /**
     * @see IDaoPropositionAG#exclureReintegrerPropositionAG(Integer, boolean)
     */
    @Override
	@Transactional(propagation = Propagation.REQUIRED)
    public void exclureReintegrerPropositionAG(Integer propositionId, boolean promouvable) {
        try {
            if (propositionId == null)
                return;
            // MANTIS 21152 SROM 09/2013 : on update aussi le booléen exclueManuellement
            Query query = getSession().createQuery(
                    "update from PropositionAG prop set " +
                    "prop.promouvable = :promouvable, " +
                    "prop.exclueManuellement = :exclueManuellement " +
                    "where prop.id = :propositionId");
            query.setInteger("propositionId", propositionId);
            query.setBoolean("promouvable", promouvable);
            query.setBoolean("exclueManuellement", !promouvable);
            query.executeUpdate();
        } catch (HibernateException hibEx) {
            throw new DaoException(hibEx);
        }
    }

    /**
     * @see IDaoPropositionAG#changeVerifieePropositionAG(Integer,EnumVerifierAG)
     */
    @Override
	@Transactional(propagation = Propagation.REQUIRED)
    public void changeVerifieePropositionAG(Integer propositionId, EnumVerifierAG enumVerifier) {
        try {
            if (propositionId == null)
                return;
            Query query = getSession().createQuery(
                    "update from PropositionAG prop set prop.verifie = :verifier where prop.id = :propositionId");
            query.setInteger("propositionId", propositionId);
            query.setParameter("verifier", enumVerifier);
            query.executeUpdate();
        } catch (HibernateException hibEx) {
            throw new DaoException(hibEx);
        }
    }

    /**
     * @see IDaoPropositionAG#changePromuPropositionAG(Integer propositionId, boolean)
     */
    @Override
	@Transactional(propagation = Propagation.REQUIRED)
    public void changePromuPropositionAG(Integer propositionId, boolean promu) {
        try {
            if (propositionId == null)
                return;
            Query query = getSession().createQuery(
                    "update from PropositionAG prop set prop.promu = :promu where prop.id = :propositionId");
            query.setInteger("propositionId", propositionId);
            query.setBoolean("promu", promu);
            query.executeUpdate();
        } catch (HibernateException hibEx) {
            throw new DaoException(hibEx);
        }
    }
    
    /**
     * @see IDaoPropositionAG#changePromuSPVPropositionAG(Integer propositionId, boolean)
     */
    @Override
	@Transactional(propagation = Propagation.REQUIRED)
    public void changePromuSPVPropositionAG(Integer propositionId, boolean promu) {
        try {
            if (propositionId == null)
                return;
            Query query = getSession().createQuery(
                    "update from PropositionAG prop set prop.promuSPV = :promu where prop.id = :propositionId");
            query.setInteger("propositionId", propositionId);
            query.setBoolean("promu", promu);
            query.executeUpdate();
        } catch (HibernateException hibEx) {
            throw new DaoException(hibEx);
        }
    }
    
    /**
     * @see fr.sedit.grh.coeur.ca.avg.dao.IDaoPropositionAG#updatePromuForPromouvableAndTableauReclassement(java.lang.Integer)
     */
    @Override
	@Transactional(propagation = Propagation.REQUIRED)
    public void updatePromuForPromouvableAndTableauReclassement(final Integer idTableau) {
        try {
            final StringBuffer hql = new StringBuffer();
            hql.append("UPDATE FROM PropositionAG prop set prop.promu = :promu, prop.dateNomination = prop.dateRetenue, prop.dateEffet = prop.dateRetenue");
            hql.append(" WHERE prop.tableauAG.id = :idTableau and prop.promouvable = :promouvable");
            final Query query = getSession().createQuery(hql.toString());
            query.setInteger("idTableau", idTableau);
            query.setBoolean("promu", true);
            query.setBoolean("promouvable", true);
            query.executeUpdate();
        } catch (HibernateException hibEx) {
            throw new DaoException(hibEx);
        }
    }

    /**
     * @see IDaoPropositionAG#changeDateOfPropositionAG(Integer, String , Date)
     */
    @Override
	@Transactional(propagation = Propagation.REQUIRED)
    public void changeDateOfPropositionAG(Integer propositionId, String nomChamps, Date date) {
        try {
            if (propositionId == null)
                return;
            String requete = null;
            if (nomChamps != null && nomChamps.length() > 0) {
                if (nomChamps.equals("dateNomination")) {
                    requete = "update PropositionAG prop set prop.dateNomination = :date where prop.id = :propositionId";
                } else if (nomChamps.equals("dateEffet")) {
                    requete = "update PropositionAG prop set prop.dateEffet = :date where prop.id = :propositionId";
                }
            } else
                return;
            if (requete == null)
                return;
            Query query = getSession().createQuery(requete);
            query.setInteger("propositionId", propositionId);
            query.setDate("date", date);
            int nbMaj = query.executeUpdate();
            // si pas de mise à jour, c'est un cas d'erreur métier.
            if (nbMaj == 0) {
                throw new DaoException(TypeDaoException.INVALID_PARAMETER);
            }
        } catch (HibernateException hibEx) {
            throw new DaoException(hibEx);
        }
    }

    /**
     * @see IDaoPropositionAG#findListAvisAGDTOByPropositionId(Integer)
     */
    @Override
	@SuppressWarnings("unchecked")
    @Transactional(propagation = Propagation.SUPPORTS, readOnly = true)
    public List<AvisAGDTO> findListAvisAGDTOByPropositionId(Integer propositionId) {
        try {
            if (propositionId == null)
                return null;
            Query queryAvis = getSession().createQuery(
                    "select avis.id as id , avis.version as version, " + "avis.appreciation.id as appreciationId, "
                            + "avis.appreciation.libelle as appreciationLibelle, "
                            + "avis.commentaire as commentaire, " + "avis.dateAvis as dateAvis, "
                            + "agent.matricule as matriculeAgent, " + "agent.nom as nomAgent, "
                            + "avis.ordre as ordre, " + "agent.prenom as prenomAgent, " + "avis.qualite as qualite, "
                            + "avis.valeurAppreciation as valeurAppreciation " +

                            "from AvisAG as avis inner join avis.agentResponsable as agent "
                            + "where avis.propositionAG.id = :propId " + "order by avis.ordre");
            queryAvis.setInteger("propId", propositionId);
            queryAvis.setResultTransformer(Transformers.aliasToBean(AvisAGDTO.class));
            List<AvisAGDTO> listAvis = queryAvis.list();
            return listAvis;
        } catch (HibernateException hibEx) {
            throw new DaoException(hibEx);
        }
    }
    
    /**
     * @see IDaoPropositionAG#findListConditionDePropositionRemplieByPropositionId(Integer)
     */
    @Override
	@SuppressWarnings("unchecked")
    @Transactional(propagation = Propagation.SUPPORTS, readOnly = true)
    public List<ConditionDeProposition> findListConditionDePropositionRemplieByPropositionId(Integer propositionId) {
        try {
            if (propositionId == null)
                return null;

            StringBuffer requeteCondition = new StringBuffer(547);
            requeteCondition.append( " from ConditionDeProposition conditionDeProposition"
                    + " inner join fetch conditionDeProposition.propositionAG  prop " 
                    + " inner join fetch conditionDeProposition.conditionAG ligneConditionAG " 
                    + " where prop.id = :idprop "
                    + " and conditionDeProposition.conditionRemplie = true "
                    // MANTIS 15498 DSAM 06/2012 : tri sur le ligneConditionAG.numOrdre ASC
                    + " order by ligneConditionAG.numOrdre asc ");

            Query queryCondition = getSession().createQuery(requeteCondition.toString());
            queryCondition.setInteger("idprop", propositionId);
            List<ConditionDeProposition> listConditionDeProposition = queryCondition.list();
            return listConditionDeProposition;
        } catch (HibernateException hibEx) {
            throw new DaoException(hibEx);
        }
    }
    
    

    /**
     * @see IDaoPropositionAG#findListConditionRemplieByPropositionDTO(Integer)
     */
    @Override
	@SuppressWarnings("unchecked")
    @Transactional(propagation = Propagation.SUPPORTS, readOnly = true)
    public List<ConditionRemplieByPropositionDTO> findListConditionRemplieByPropositionDTO(Integer propositionId) {
        try {
            if (propositionId == null)
                return null;

            StringBuffer requeteCondition = new StringBuffer(547);
            requeteCondition.append("select detail.id as id, detail.version as version, "
                    + " detail.ancienneteAnnee as ancienneteAnnee, " 
                    + "detail.ancienneteJour as ancienneteJour, " 
                    + "detail.dateDetail as dateDetail, "
                    + " val.texteCondition as  texteCondition, " 
                    + " val.numOrdre as numOrdreValeurCondition, "
                    + " val.ligneConditionAG.id as ligneConditionId, "
                    + " condition.dateMini as dateMini, " 
                    + " condition.id as conditionDePropositionId, "
                    + " ligneConditionAG.numOrdre as numOrdreLigneConditionAG "
                    + " from DetailCondition as detail " 
                    + " inner join detail.valeurFonctionCondition as val "
                    + " inner join val.ligneConditionAG as ligneConditionAG "
                    + " inner join detail.conditionDeProposition as condition "
                    + " inner join condition.propositionAG as prop " 
                    + " where prop.id = :idprop "
                    + " and condition.conditionRemplie = true " 
                    + " order by ligneConditionAG.numOrdre, val.numOrdre ");

            // requeteCondition.append("select condition.id as id , condition.version as version , " +
            // "val.texteCondition as texteCondition,condition.dateMini as dateMini, " +
            // " detail.ancienneteAnnee as ancienneteAnnee, detail.ancienneteJour as ancienneteJour ");
            // requeteCondition.append("from PropositionAG as prop, ConditionDeProposition as condition, ");
            // requeteCondition.append("DetailCondition as detail inner join detail.valeurCritereCondition as val ");
            // requeteCondition.append("inner join val.ligneConditionAG as ligne ");
            // requeteCondition.append("where prop.id = :idprop ");
            // requeteCondition.append("and condition.propositionAG = prop ");
            // requeteCondition.append("and condition.conditionRemplie = true ");
            // requeteCondition.append("and condition.conditionAG = ligne ");
            // requeteCondition.append("order by val.numOrdre ");

            Query queryCondition = getSession().createQuery(requeteCondition.toString());
            queryCondition.setInteger("idprop", propositionId);
            queryCondition.setResultTransformer(Transformers.aliasToBean(ConditionRemplieByPropositionDTO.class));
            List<ConditionRemplieByPropositionDTO> listConditionRemplieByPropositionDTO = queryCondition.list();
            return listConditionRemplieByPropositionDTO;
        } catch (HibernateException hibEx) {
            throw new DaoException(hibEx);
        }
    }

    /*------------------------------------------------------------------------------	
     * 
     * FIN : code de chargement de propositions PROMOUVABLES 
     
     * DEBUT :  code de chargement de propositions NON PROMOUVABLES 
     * 
     -------------------------------------------------------------------------------*/
    private final String selectPropNonPromouvable = "select" + " prop.id as id," + " prop.version as version,"
            + " prop.verifie as verifiee, " + " agent.id as idAgent, " + " agent.nom as nomAgent,"
            + " agent.prenom as prenomAgent," + " agent.matricule as matriculeAgent,"
            + " service.libelle as libelleService," + " statut.libelle as libelleStatut,"
            + " grade.libelleMoyen as libelleGrade," + " chevAnc.code as codeChevronAncien,"
            + " prop.typeProposition as typeProposition , " + " prop.dateRetenue as dateRetenue, "
            + " prop.reliquat as reliquat, " + " prop.dateEntreeEchelon as dateEntreeEchelon, "
            + " prop.dateEntreeSPVActuel as dateGradeSPV ";

    private final String addSelectPropNPAgentEnErreur = ", prop.msgControle as msgControle ";

    private final String addSelectPropNPRetardee = ", prop.dureeRetardAnnee as dureeRetardAnnee, "
            + " prop.dureeRetardJour as dureeRetardJour ";

    private final String addSelectPropNPExclue = ", prop.motifExclusion as motifExclusion   ";

    private final String addSelectPropNPBloquee = ", positionAdmin.libelle as motifBlocage ";

    private final String addFromPropNPBloquee = " inner join prop.fichePosition as fichePosiAdmin "
            + " inner join fichePosiAdmin.positionAdmin as positionAdmin ";

    /**
     * @see IDaoPropositionAG#findListPropositionNonPromouvableAGDTO(Map, Map, long, long, String)
     */
    @Override
	@SuppressWarnings("unchecked")
    @Transactional(propagation = Propagation.SUPPORTS, readOnly = true)
    public List<PropositionNonPromouvableAGDTO> findListPropositionNonPromouvableAGDTO(Map<String, Integer> tri,
            Map<String, Object> criteres, long firstLine, long limitLine, String filter) {
        try {
            String requete = null;
            EnumTypePropositionAG enumTypePropositionAG = (EnumTypePropositionAG) criteres.get("enumTypePropositionAG");
            if (enumTypePropositionAG != null) {
                if (EnumTypePropositionAG.EXCLUE.equals(enumTypePropositionAG)) {
                    requete = selectPropNonPromouvable + addSelectPropNPExclue + fromPropoposition;
                }
                if (EnumTypePropositionAG.PROMOUVABLE.equals(enumTypePropositionAG)) {
                    requete = selectPropNonPromouvable + addSelectPropNPExclue + fromPropoposition;
                }
                if (EnumTypePropositionAG.BLOQUEE.equals(enumTypePropositionAG)) {
                    requete = selectPropNonPromouvable + addSelectPropNPBloquee + fromPropoposition
                            + addFromPropNPBloquee;
                }
                if (EnumTypePropositionAG.RETARDEE.equals(enumTypePropositionAG)) {
                    requete = selectPropNonPromouvable + addSelectPropNPRetardee + fromPropoposition;
                }
                if (EnumTypePropositionAG.ERREUR.equals(enumTypePropositionAG)) {
                    requete = selectPropNonPromouvable + addSelectPropNPAgentEnErreur + fromPropopositionAgentEnErreur;
                }
                if (EnumTypePropositionAG.NMOINS1.equals(enumTypePropositionAG)) {
                    requete = selectPropNonPromouvable + fromPropoposition;
                }

            }
            if (requete == null)
                return new ArrayList<PropositionNonPromouvableAGDTO>(0);
            Query query = prepareQueryListPropositions(tri, criteres, requete, null, true);
            if (query == null)
                return new ArrayList<PropositionNonPromouvableAGDTO>(0);
            this.applyLimits(filter, query, (int)firstLine, (int)limitLine);
            query.setResultTransformer(Transformers.aliasToBean(PropositionNonPromouvableAGDTO.class));

            List<PropositionNonPromouvableAGDTO> listResult = (List<PropositionNonPromouvableAGDTO>) this.filterList(filter, query.list());
            
            // Libelle Grade SPV
            if(listResult != null){
	            for(PropositionNonPromouvableAGDTO propositionNonPromouvableAGDTO :listResult){
	            	if(propositionNonPromouvableAGDTO.getDateGradeSPV() != null){
                    	Query queryGradeSPV  = getSession().createQuery("SELECT prop.gradeSPVActuel FROM PropositionAG prop WHERE prop.id = :idProp");
                    	queryGradeSPV.setInteger("idProp", propositionNonPromouvableAGDTO.getId());
                    	Grade result = (Grade)queryGradeSPV.uniqueResult();
                    	if(result != null){
                    		propositionNonPromouvableAGDTO.setLibelleGradeSPV(result.getLibelleMoyen());
                    	}
                    }
	            }
            }
            return listResult;
        } catch (HibernateException hibEx) {
            throw new DaoException(hibEx);
        }
    }

    @Override
	@Transactional(propagation = Propagation.SUPPORTS, readOnly = true)
    public PropositionAG loadCompletePropositonAGById(Integer propositionId, final boolean lock) {
        try {
            if (propositionId==null){return null;}
            StringBuffer sbHql = new StringBuffer(844);
            sbHql.append("from PropositionAG prop" 
                    + " left join fetch prop.tableauAG tab"
                    + " left join fetch tab.organisme" 
                    + " left join fetch prop.reussiteConcours"
                    + " left join fetch prop.chevronCible" 
                    + " left join fetch prop.chevronAncien"
                    + " left join fetch prop.gradeAncien" 
                    + " left join fetch prop.gradeCible gradeCible"
                    // GIFT 92268 - LDEC - 12 août 2010 - ListHistoGrade est utilisé donc il faut le précharger 
                    + " left join fetch gradeCible.listHistoGrade"
                    + " inner join fetch prop.carriere as car" 
                    + " left join fetch car.collectivite "
                    + " left join fetch car.libelleCarriere "
                    + " left join fetch prop.statutActuel"
                    + " left join fetch prop.rangClassementAG"
                    + " left join fetch prop.listConditionDeProposition cond"
                    + " left join fetch cond.listDetailCondition" 
                    + " left join fetch cond.conditionAG"
                    + " left join fetch prop.listAvisAG" 
                    + " left join fetch prop.motifAvancement"
                    + " left join fetch prop.statutAvancement" 
                    + " left join fetch prop.propositionAGAnneePrecedente"
                    + " left join fetch prop.conditionDePropositionSelectionnee" 
                    + " left join fetch prop.imprime"
                    + " left join fetch prop.fichePosition as fiche" 
                    + " left join fetch fiche.positionAdmin"
                    + " where prop.id = :propositionId");
            Query query = getSession().createQuery(sbHql.toString());
            query.setInteger("propositionId", propositionId);
            if (lock) {
                // Mantis DA 5620 - LDEC - 01/06/2010 - Le lancement concurrent de plusieurs controle injection fesait planté tout les lancement concurrent sauf 1.
                query.setLockMode("prop", LockMode.UPGRADE);
            }
            return (PropositionAG) query.uniqueResult();
        } catch (HibernateException hibEx) {
            throw new DaoException(hibEx);
        }
    }

    @Override
	@Transactional(propagation = Propagation.SUPPORTS, readOnly = true)
    public Long countExtraValidPropositionsAG(PropositionAG propositionAG) {
        try {
            StringBuffer sbHql = new StringBuffer(245);
            sbHql.append("select count(*)").append(" from PropositionAG prop").append(
                    " left join prop.tableauAG as tableau").append(" with tableau.etatTableau > :etatTableau").append(
                    " where prop.carriere = :carriere").append(" and prop.dateInjection is null").append(
                    " and prop.promouvable = true").append(" and prop != :proposition");

            // Confidentialité
            HqlSecure hqls = getSecurityFilter().secureClause(SecurityMgrGRH.CONFIDENTIALITE_AGENT, "prop.carriere.agent");
            if (hqls != null && hqls.where.length() > 0) {
                sbHql.append(" and").append(hqls.where);
            }
            Query query = getSession().createQuery(sbHql.toString());

            query.setEntity("proposition", propositionAG);
            query.setParameter("etatTableau", EnumPhaseTableauAG.ELABORATION_TABLEAU);
            query.setEntity("carriere", propositionAG.getCarriere());

            if (hqls != null && hqls.criteriaValues != null) {
                query.setProperties(hqls.criteriaValues);
            }

            log.debug("Compte le nombre de propositionAG valides pour proposition != " + propositionAG.getId());
            return (Long) query.uniqueResult();
        } catch (HibernateException hibExc) {
            throw new DaoException(hibExc);
        }
    }

    /*------------------------------------------------------------------------------	
     * 
     * FIN : code de chargement de propositions NON PROMOUVABLES 
     
     * DEBUT :  export des propositions
     * 
     -------------------------------------------------------------------------------*/
    /**
     * @see IDaoPropositionAG#exportPropositionAGForExcel(Map)
     */
    @Override
	@Transactional(propagation = Propagation.SUPPORTS, readOnly = true)
    public List<PropositionAGAExporterDTO> exportPropositionAGForExcel(Map<String, Object> criteres,Boolean isGpecInstalled) {
        try {
            /* MANTIS 15971 SROM 07/2012 : gestion d'un UNION factice (le classique ne marche pas avec hibernate) 
             * pour récuperer d'un côté les agents liés à une UG de type AE et d'un autre côté les autres agents */
            List<PropositionAGAExporterDTO> list = queryExportAvecUniteGestionAG(criteres,isGpecInstalled);
            
            List<Integer> listIdPropDejaRecup = new ArrayList<Integer>();
            for (PropositionAGAExporterDTO prop : list) {
                listIdPropDejaRecup.add(prop.getId());
            }
            list.addAll(queryExportSansUniteGestionAG(criteres, listIdPropDejaRecup));
            
            int size = list.size();
            for (int i = 0; i < size; i++) {
                PropositionAGAExporterDTO prop = list.get(i);
                List<AvisAGDTO> listAvis = findListAvisAGDTOByPropositionId(prop.getId());
                if (listAvis != null && !listAvis.isEmpty()) {
                    prop.setListAvisAGDTO(listAvis);
                }
            }
            for (int i = 0; i < size; i++) {
                // renseignement des valeurs et saisie libre de rang de classement s'il y en a un lier a la proposition
                PropositionAGAExporterDTO prop = list.get(i);
                // renseignement des conditions s'il y en a un lier a la proposition
                List<ConditionDeProposition> listConditionRemplie = findListConditionDePropositionRemplieByPropositionId(prop.getId());
                prop.setListConditionAGRemplie(listConditionRemplie);
                // Mantis 35981: RHWEB AVG –Tableau d’avancement- Export- Ajout des colonnes manquantes dans l’export des promouvables.
                Date dateOldCarSynth = prop.getDateEffet() == null ? prop.getDateRetenue() : prop.getDateEffet();
                if(dateOldCarSynth == null){
                	dateOldCarSynth = prop.getDateCalcul();
                }
                if(dateOldCarSynth!=null){
                	//-- mantis 0035981: RHWEB AVG –Tableau d’avancement- Export- Ajout des colonnes manquantes dans l’export des promouvables.
                	//-- AMAI on prend la veille de la date d'effet car la grille indiciaire dans le cas d'un reclassement sont maj et donc visible en carsynt
                	//avant injection de la proposition
                	dateOldCarSynth = UtilsDate.addDays(dateOldCarSynth, -1);
                }
                boolean isInject = prop.getDateInjection() != null;
                CarriereSynthetique carSynth = loadAncienCarSynth(prop.getId(), dateOldCarSynth, isInject);
                if(carSynth != null){
	                prop.setAncienIndiceBrut(carSynth.getIndiceBrut());
	                prop.setAncienIndiceMaj(carSynth.getIndiceMajore());
	                prop.setAncienReliquat(carSynth.getReliquatAncienneteEchelon());
	                if (carSynth.getFonction() != null) {
	                	prop.setCodeFonction(carSynth.getFonction().getCode());
	                	prop.setLibFonction(carSynth.getFonction().getLibelle());
	                }
                }
            }
            return list;
        } catch (HibernateException hibExc) {
            throw new DaoException(hibExc);
        }
    }

    private CarriereSynthetique loadAncienCarSynth(Integer idProposition, Date dateOldCarSynth, boolean isInject) {
    	try {
            StringBuffer requete = new StringBuffer("SELECT cs FROM PropositionAG as prop")
            	.append(" LEFT JOIN prop.carriere as car")
                .append(" LEFT JOIN car.listCarriereSynthetique cs ")
                .append(" WHERE prop.id = :propositionId")
                .append(" AND cs.dateDebut <= :dateOldCarSynth");
            if(isInject){
            	requete.append(" AND (cs.dateFin = :dateOldCarSynthMoins1 or cs.dateFin = :dateOldCarSynth)");
            }else{
            	requete.append(" AND (cs.dateFin is null or cs.dateFin >= :dateOldCarSynth )");
            }

            Query query = getSession().createQuery(requete.toString());
            query.setInteger("propositionId", idProposition);
            query.setDate("dateOldCarSynth", dateOldCarSynth);
            if(isInject){
            	query.setDate("dateOldCarSynthMoins1", UtilsDate.addDays(dateOldCarSynth,-1));
            }
            List<CarriereSynthetique> listCarSynth = query.list();
            // si on a des non gérées, on les prends en priorité
            CarriereSynthetique carSynth = null;
            if(listCarSynth != null){
            	for(CarriereSynthetique cs : listCarSynth){
            		carSynth = cs;
            		if(!cs.getEstGere()){
            			break;
            		}
            	}
            }
            return carSynth;
        } catch (HibernateException hibExc) {
            throw new DaoException(TypeDaoException.FATAL, hibExc);
        }
	}

	/**
     * Génère les exports des propositions liées à une unité de gestion AG
     * @param criteres
     * @return
     */
    @SuppressWarnings("unchecked")
    private List<PropositionAGAExporterDTO> queryExportAvecUniteGestionAG(Map<String, Object> criteres,Boolean isGpecInstalled) {
        try {
            Query query;
            StringBuffer hql = new StringBuffer(4256);
            hql.append("SELECT DISTINCT prop.id AS id, ")
                    .append("prop.version AS version, ")
                    .append("uniteGestion.code as codeUniteGestion, ")
                    .append("uniteGestion.libelle as libelleUniteGestion, ")
                    .append("collectivite.code AS codeCollectivite, ")
                    .append("collectivite.libelle AS libelleCollectivite, ")
                    .append("agent.id AS idAgent, ")
                    .append("agent.nom AS nomAgent, ")
                    .append("agent.nomJeuneFille AS nomJeuneFilleAgent, ")
                	.append("agent.prenom AS prenomAgent, ")
                    .append("agent.matricule AS matriculeAgent, ")
                    .append("agent.sexe AS sexeAgent, ")
                    .append("agent.dateNaissance AS dateNaissance, ")
                    .append("agent.date1ereTitularisation AS datePremiereTitularisation, ")
                    .append("agent.dateEntreeFP AS dateEntreeAgentFonctionPublique, ")
                    .append("agent.dateEntreeFPT AS dateEntreeAgentFonctionPubliqueTerritoriale, ")
                    .append("agent.dateEntreeFonctionnaireFP AS dateEntreeFonctionnaireFonctionPublique, ")
                    .append("agent.dateEntreeFonctionnaireFPT AS dateEntreeFonctionnaireFonctionPubliqueTerritoriale, ")
                    .append("filiere.codeTri AS codeTriFiliere, ")
                    .append("filiere.code AS codeFiliere, ")
                    .append("filiere.libelle AS libelleFiliere,   ")
                    .append("categorie.code AS codeCategorie, ")
                    .append("categorie.libelle AS libelleCategorie, ")
                    .append("groupeHierarchique.code AS codeGroupeHierarchique, ")
                    .append("groupeHierarchique.libelle AS libelleGroupeHierarchique, ")
                    .append("cadEmp.codeTri AS codeTriCadreEmploi, ")
                    .append("cadEmp.code AS codeCadreEmploi, ")
                    .append("cadEmp.libelle AS libelleCadreEmploi, ")
                    .append("gradeHist.NumeroOrdreDansCE AS ordreGradeDansCE, ")
                    .append("gradeAncien.id AS idGradeActuel, ")
                    .append("gradeAncien.code AS codeGrade, ")
                    .append("gradeAncien.libelleMoyen AS libelleMoyenGrade, ")
                    .append("gradeAncien.libelleLong AS libelleLongGrade, ")
                    .append("chevAnc.code AS codeEchelon, ")
                    .append("statut.code AS codeStatut, ")
                    .append("statut.libelle AS libelleStatut, ")
                    .append("service.id AS idService, ")
                    .append("service.code AS codeService, ")
                    .append("service.libelle AS libelleService, ");
            	if(isGpecInstalled){
            		hql.append("descrPoste.libelleLong AS denominationDescriptifPoste, ");
            	}else{
            		hql.append("descrPoste.denomination AS denominationDescriptifPoste, ");
            	}
                    
                    hql.append("car.principale AS carrierePrincipale, ")
                    .append("libelleCarriere.libelle AS libelleCarriere, ")
                    .append("prop.codeRegroupement AS codeRegroupement, ")
                    .append("positionAdmin.code AS codePositionAdmin, ")
                    .append("positionAdmin.libelle AS libellePositionAdmin, ")
                    .append("prop.dateEntreeEchelon AS dateEntreeEchelon, ")
                    .append("prop.dateEntreeGrade AS dateEntreeGrade, ")
                    .append("prop.dateMiniAvancement AS dateMiniAvancement, ")
                    .append("prop.reliquat AS reliquat, ")
                    .append("prop.promouvable AS promouvable, ")
                    .append("prop.typeProposition AS typeProposition, ")
                    .append("prop.dateCalcul AS dateCalcul, ")
                    .append("prop.dateEffet AS dateEffet, ")
                    .append("prop.dateInjection AS dateInjection, ")
                    .append("prop.dateNomination AS dateNomination, ")
                    .append("prop.dateRefus AS dateRefus, ")
                    .append("prop.dateRetenue AS dateRetenue, ")
                    .append("prop.decision AS decision, ")
                    .append("prop.dureeRetardAnnee AS dureeRetardAnnee, ")
                    .append("prop.dureeRetardJour AS dureeRetardJour, ")
                    .append("prop.exclueManuellement AS exclueManuellement, ")
                    .append("prop.injection AS injection, ")
                    .append("prop.modeCreation AS modeCreation, ")
                    .append("prop.motifExclusion AS motifExclusion, ")
                    .append("prop.motifRefus AS motifRefus, ")
                    .append("prop.reclassementMedical AS reclassementMedical, ")
                    .append("prop.refuse AS refuse, ")
                    .append("prop.typeAvancement AS typeAvancement, ")
                    .append("prop.verifie AS verifie, ")
                    .append("gradeCible.id AS idGradeCible, ")
                    .append("gradeCible.code AS codeGradeCible, ")
                    .append("gradeCible.libelleMoyen AS libelleMoyenGradeCible, ")
                    .append("gradeCible.libelleLong AS libelleLongGradeCible , ")
                    .append("chevronCible.code AS codeChevronCible, ")
                    .append("prop.indiceBrut as indiceBrutProposition, ")
                	.append("gradeCible.cadreStatutaire.id AS idCadreStatutaire, ")
                	.append("prop.ancienneteAA AS ancienneteAA, ")
                    .append("prop.ancienneteMM AS ancienneteMM, ")
                    .append("prop.ancienneteJJ AS ancienneteJJ, ")
                    .append("prop.motifAvancement.code AS codeMotifAvancement, ")
                    .append("prop.motifAvancement.libelle AS libelleMotifAvancement, ")
                    .append("prop.statutAvancement.code  AS statutAvancementCode, ")
                    .append("prop.statutAvancement.libelle AS statutAvancementLibelle, ")
                   
                    // rangClassementAG Soit saisieLibre soit valeur (afficher la non nul)
                    .append("rang.valeur AS rangValeur, ")
                    .append("rang.saisieLibre AS rangSaisieLibre, ")
                    .append("concoursAgent.dateObtention AS dateObtentionConcours, ")
                    
                    // conditionDePropositionSelectionnee Sélection : oui si condition selectionnée = condition de la ligne mettre oui
                    .append("prop.conditionDePropositionSelectionnee.id AS idConditionSelectionne, ")
                    .append("prop.msgControle AS msgControle, ")
                    .append("tableauAG.dateSession AS dateCAP, ")
                    .append("tableauAG.code AS codeTableauAvancement, ")
                    .append("tableauAG.libelle AS libelleTableauAvancement, ")
                    .append("tableauAG.etatTableau AS phaseTableauAvancement, ")
                   
                    // Les concours
                    .append("concoursAgent.commentaire AS reussiteConcoursCommentaire, ")
                    // GIFT IN482 - STH - 07/04/2009 - Suppression des champs commission et du lien propositionAG
                    .append("concoursAgent.dateObtention AS reussiteConcoursDateObtention, ")
                    .append("concoursAgent.justificatifsFournis AS reussiteConcoursJustificatifsFournis, ")
                    .append("concoursAgent.typeConcours AS reussiteConcoursTypeConcours, ")
                    .append("concours.libelle AS concoursLibelle, ")
                    .append("gradeConcours.libelleLong AS concoursLibelleGrade ")
                    
                     //36103 : ajout des dates de calcul et entrée dans la collect
                    .append(",(select max(grille.dateCalcul) from GrilleAnciennete as grille where grille.carriere = prop.carriere and grille.codeRegroupement = prop.codeRegroupement) AS dateCalculGrilleAnciennete ")
                    
                    //ESRH-2152 : revue du calcul de la date d'entrée dans la collectivité
                    .append(" , (SELECT MIN(fad.dateDebut) FROM fad WHERE fad.agent.id=agent.id ")
                    .append(" AND fad.collectivite=car.collectivite) as dateEntreeAgentCollect, ")
                    .append(" prop.promu AS promu ")
                    // ESRH-6729
                    .append(", car.id as carriereId ")
                    
                    .append("FROM PropositionAG AS prop ")
                    .append("INNER JOIN prop.statutActuel AS statut ")
                    .append("INNER JOIN prop.carriere AS car ")
                    .append("INNER JOIN car.collectivite AS collectivite ")
                    .append("INNER JOIN car.agent AS agent ")
                    .append("LEFT JOIN agent.listFicheArriveeDepart as fad ")
                    .append("left join agent.listAgentUniteGestion as agtUnitGes ")
                    .append("left join agtUnitGes.uniteGestion as uniteGestion ")
                    .append("left join agtUnitGes.module as module with (module.code = :moduleCode) ")
                    .append("INNER JOIN car.libelleCarriere AS libelleCarriere ")
                    .append("INNER JOIN prop.fichePosition AS fichePosAdmin ")
                    .append("INNER JOIN fichePosAdmin.positionAdmin AS positionAdmin ")
                    .append("LEFT JOIN prop.gradeAncien AS gradeAncien ")
                    .append("LEFT JOIN prop.gradeCible AS gradeCible ")
                    .append("LEFT JOIN prop.chevronAncien AS chevAnc ")
                    .append("LEFT JOIN prop.chevronCible AS chevronCible ")
                    .append("LEFT JOIN prop.rangClassementAG AS rang ")
                    .append("LEFT JOIN prop.tableauAG AS tableauAG ")
                    .append("LEFT JOIN prop.motifAvancement AS motifAvancement ")
                    .append("LEFT JOIN prop.statutAvancement AS statutAvancement ")
                    .append("LEFT JOIN car.listFicheAffectation AS ficAffect WITH (ficAffect.dateDebut <= :date AND NVL(ficAffect.dateFin, (:tomorrow)) >= (:tomorrow)) ")
                    .append("LEFT JOIN ficAffect.service AS service ");
            		if(!isGpecInstalled){
            			hql.append("LEFT JOIN car.listFichePoste AS ficPoste WITH (ficPoste.dateDebut <= :date AND NVL(ficPoste.dateFin, (:tomorrow)) >= (:tomorrow)) ");
            			hql.append("LEFT JOIN ficPoste.poste AS poste ");
            			hql.append("LEFT JOIN poste.listDescriptifPoste AS descrPoste WITH (descrPoste.dateDebut <= :date AND NVL(descrPoste.dateFin, (:tomorrow)) >= (:tomorrow)) ");
            		}else{
            			hql.append("LEFT JOIN car.listFicheAffectationPostes AS ficPoste WITH (ficPoste.dateDebut <= :date AND NVL(ficPoste.dateFin, (:tomorrow)) >= (:tomorrow)) ");
            			hql.append("LEFT JOIN ficPoste.posteTravailEntete AS poste ");
            			hql.append("LEFT JOIN poste.listPosteTravail AS descrPoste WITH (descrPoste.dateDebut <= :date AND NVL(descrPoste.dateFin, (:tomorrow)) >= (:tomorrow)) ");
            			
            		}
            			
            		
            		hql.append("LEFT JOIN prop.reussiteConcours AS concoursAgent ")
                    .append("LEFT JOIN concoursAgent.concours AS concours ")
                    .append("LEFT JOIN concours.gradeCible AS gradeConcours, ")
                    .append("HistoGrade AS gradeHist ")
                    .append("INNER JOIN gradeHist.grade AS grade  ")
                    .append("INNER JOIN gradeHist.categorie AS categorie  ")
                    .append("LEFT JOIN gradeHist.groupeHierarchique AS groupeHierarchique,  ")
                    .append("HistoCadreEmploi AS cadEmpHist ")
                    .append("INNER JOIN cadEmpHist.cadreEmploi AS cadEmp ")
                    .append("INNER JOIN cadEmpHist.filiere AS filiere ")
                    
                    .append("WHERE gradeHist.cadreEmploi = cadEmp ")
                    .append("AND prop.gradeCible = grade ")
                    .append("AND gradeHist.dateDebut <= :date ")
                    .append("AND NVL(gradeHist.dateFin, (:tomorrow)) >= (:tomorrow) ")
                    .append("AND cadEmpHist.cadreEmploi = cadEmp ")
                    .append("AND cadEmpHist.dateDebut <= :date ")
                    .append("AND NVL(cadEmpHist.dateFin, (:tomorrow)) >= (:tomorrow) ") 
                    .append("and (module.code is null or module.code = :moduleCode) ");

            Integer tableauAGId = (Integer)criteres.get("tableauAGId");
            if (tableauAGId != null) {
                hql.append("AND tableauAG.id = :tableauAGId ");
            } else {
                hql.append("AND tableauAG IS NULL ");
            }

            Boolean promouvable = (Boolean) criteres.get("promouvable");
            Boolean nonPromouvable = (Boolean) criteres.get("nonPromouvable");
            Boolean promu = (Boolean) criteres.get("promuExport");
            if (promouvable != null && nonPromouvable != null && promu !=null) {
                // avec les promus
                if(promu == Boolean.TRUE){
                    // promus ou promouvables
                    if (promouvable == Boolean.TRUE && nonPromouvable == Boolean.FALSE) {
                        hql.append("AND (prop.promouvable = true OR prop.promu = true )");
                    }
                    // promus ou non promouvables
                    else if (promouvable == Boolean.FALSE && nonPromouvable == Boolean.TRUE) {
                        hql.append("AND (prop.promouvable = false OR prop.promu = true ) ");
                    }  
                    // promus ou promouvables ou non promouvables
                    else if (promouvable == Boolean.TRUE && nonPromouvable == Boolean.TRUE) {
                       // hql.append(" AND (prop.promouvable = false OR prop.promouvable = true OR prop.promu = true) ");
                    }
                    // que les promus
                    else if(promouvable == Boolean.FALSE && nonPromouvable == Boolean.FALSE) {
                        hql.append(" AND prop.promu = true ");
                    }
                }
                // sans les promus
                else{
                    // sans les promus et que les promouvables
                    if (promouvable == Boolean.TRUE && nonPromouvable == Boolean.FALSE) {
                        hql.append("AND prop.promouvable = true AND prop.promu = false");
                    }
                    // sans les promus et que les non promouvables
                    else if (promouvable == Boolean.FALSE && nonPromouvable == Boolean.TRUE) {
                        hql.append("AND prop.promouvable = false ");
                    }
                    // sans promus et que les non promouvables et les promouvables
                    else if(promouvable == Boolean.TRUE && nonPromouvable == Boolean.TRUE){
                        hql.append("AND prop.promu = false ");
                    }
                }
            }

            // filtre pour les concours
            Boolean withConcoursAgent = criteres.containsKey("withConcoursAgent");
            Boolean withConcours = criteres.containsKey("withConcours");
            if (withConcoursAgent) {
                hql.append("AND concoursAgent IS NOT NULL ");
            }
            if (withConcours) {
                hql.append("AND concours IS NOT NULL ");
            }

            String gradeId = (String) criteres.get("gradeId");
            if (gradeId != null) {
                hql.append("AND gradeCible.id = :gradeId ");
            }

            String filiereId = (String) criteres.get("filiereId");
            if (filiereId != null) {
                hql.append("AND filiere.id = :filiereId ");
            }

            String cadreEmploiId = (String) criteres.get("cadreEmploiId");
            if (cadreEmploiId != null) {
                hql.append("AND cadEmp.id = :cadreEmploiId ");
            }

            EnumTypePropositionAG enumTypePropositionAG = (EnumTypePropositionAG) criteres.get("enumTypePropositionAG");
            if (enumTypePropositionAG != null) {
                hql.append("AND prop.typeProposition = :enumTypePropositionAG ");
            }

            EnumVerifierAG enumVerifier = (EnumVerifierAG) criteres.get("enumVerifier");
            if (enumVerifier != null) {
                hql.append("AND prop.verifie = :enumVerifier ");
            }

            String dateDebutObtention = (String) criteres.get("dateDebutObtention");
            if (dateDebutObtention != null) {
                hql.append("AND concoursAgent.dateObtention >= :dateDebutObtention ");
            }

            String dateFinObtention = (String) criteres.get("dateFinObtention");
            if (dateFinObtention != null) {
                hql.append("AND concoursAgent.dateObtention <= :dateFinObtention ");
            }

            EnumDecisionCAP decision = (EnumDecisionCAP) criteres.get("EnumDecisionCAP");
            if (decision != null) {
                hql.append("AND prop.decision = :decision ");
            }
            // fin filtre pour les concours

            String collectiviteId = (String) criteres.get("collectiviteId");
            if (collectiviteId != null) {
                hql.append("AND car.collectivite.id = :collectiviteId ");
            }
            query = getSession().createQuery(hql.toString());
            
            // MANTIS 35489 SROM 01/2017 : on borne la date de réference aux dates du tableau s'il existe + on supprime la notion de tomorrow qui est inexacte : il faut une seule date de réference
            Date date = getDateRefFromTableauAG(tableauAGId);
            //Date tomorrow = UtilsDate.addDays(date, 1);
            query.setDate("date", date);
            query.setDate("tomorrow", date);
            //query.setDate("tomorrow", tomorrow);
            
            if (tableauAGId != null)
                query.setInteger("tableauAGId", tableauAGId);

            if (collectiviteId != null)
                query.setString("collectiviteId", collectiviteId);

            query.setString("moduleCode", EnumModuleCode.AV.getCode());
            
            // Application des filtres sur concours
            try {
                if (dateDebutObtention != null) {
                    query.setDate("dateDebutObtention", UtilsDate.Formatage.DATE_FORMATTER.parse(dateDebutObtention));
                }
                if (dateFinObtention != null) {
                    query.setDate("dateFinObtention", UtilsDate.Formatage.DATE_FORMATTER.parse(dateFinObtention));
                }

            } catch (ParseException e) {
                throw new TechnicalException(TechnicalException.Type.UNEXPECTED_EXCEPTION, e);
            }

            if (gradeId != null) {
                query.setString("gradeId", gradeId);
            }
            if (cadreEmploiId != null) {
                query.setString("cadreEmploiId", cadreEmploiId);
            }
            if (filiereId != null) {
                query.setString("filiereId", filiereId);
            }
            if (decision != null) {
                query.setParameter("decision", decision);
            }

            query.setResultTransformer(Transformers.aliasToBean(PropositionAGAExporterDTO.class));
            List<PropositionAGAExporterDTO> list = query.list();
            int size = list.size();
            for (int i = 0; i < size; i++) {
                PropositionAGAExporterDTO prop = list.get(i);
                List<AvisAGDTO> listAvis = findListAvisAGDTOByPropositionId(prop.getId());
                if (listAvis != null && !listAvis.isEmpty()) {
                    prop.setListAvisAGDTO(listAvis);
                }
            }
            for (int i = 0; i < size; i++) {
                // renseignement des valeurs et saisie libre de rang de classement s'il y en a un lier a la proposition
                PropositionAGAExporterDTO prop = list.get(i);
                // renseignement des conditions s'il y en a un lier a la proposition
                List<ConditionDeProposition> listConditionRemplie = findListConditionDePropositionRemplieByPropositionId(prop.getId());
                prop.setListConditionAGRemplie(listConditionRemplie);
            }
            return list;
        } catch (HibernateException hibExc) {
            throw new DaoException(hibExc);
        }
    }

    /**
     * Génère les exports des propositions liées à une unité de gestion AG
     * @param criteres
     * @return
     */
    @SuppressWarnings("unchecked")
    private List<PropositionAGAExporterDTO> queryExportSansUniteGestionAG(Map<String, Object> criteres, List<Integer> listIdPropDejaRecup) {
        try {
            Query query;
            StringBuffer hql = new StringBuffer(4256);
            hql.append("SELECT DISTINCT prop.id AS id, ")
                    .append("prop.version AS version, ")
                    .append("'' as codeUniteGestion, ")
                    .append("'' as libelleUniteGestion, ")
                    .append("collectivite.code AS codeCollectivite, ")
                    .append("collectivite.libelle AS libelleCollectivite, ")
                    .append("agent.nom AS nomAgent, ")
                	.append("agent.nomJeuneFille AS nomJeuneFilleAgent, ")
                    .append("agent.prenom AS prenomAgent, ")
                    .append("agent.matricule AS matriculeAgent, ")
                    .append("agent.sexe AS sexeAgent, ")
                    .append("agent.dateNaissance AS dateNaissance, ")
                    .append("agent.date1ereTitularisation AS datePremiereTitularisation, ")
                    .append("agent.dateEntreeFP AS dateEntreeAgentFonctionPublique, ")
                    .append("agent.dateEntreeFPT AS dateEntreeAgentFonctionPubliqueTerritoriale, ")
                    .append("agent.dateEntreeFonctionnaireFP AS dateEntreeFonctionnaireFonctionPublique, ")
                    .append("agent.dateEntreeFonctionnaireFPT AS dateEntreeFonctionnaireFonctionPubliqueTerritoriale, ")
                    .append("filiere.codeTri AS codeTriFiliere, ")
                    .append("filiere.code AS codeFiliere, ")
                    .append("filiere.libelle AS libelleFiliere,   ")
                    .append("categorie.code AS codeCategorie, ")
                    .append("categorie.libelle AS libelleCategorie, ")
                    .append("groupeHierarchique.code AS codeGroupeHierarchique, ")
                    .append("groupeHierarchique.libelle AS libelleGroupeHierarchique, ")
                    .append("cadEmp.codeTri AS codeTriCadreEmploi, ")
                    .append("cadEmp.code AS codeCadreEmploi, ")
                    .append("cadEmp.libelle AS libelleCadreEmploi, ")
                    .append("gradeHist.NumeroOrdreDansCE AS ordreGradeDansCE, ")
                    .append("gradeAncien.code AS codeGrade, ")
                    .append("gradeAncien.libelleMoyen AS libelleMoyenGrade, ")
                    .append("gradeAncien.libelleLong AS libelleLongGrade, ")
                    .append("chevAnc.code AS codeEchelon, ")
                    .append("statut.code AS codeStatut, ")
                    .append("statut.libelle AS libelleStatut, ")
                    .append("service.id AS idService, ")
                    .append("service.code AS codeService, ")
                    .append("service.libelle AS libelleService, ")
                    .append("descrPoste.denomination AS denominationDescriptifPoste, ")
                    .append("car.principale AS carrierePrincipale, ")
                    .append("libelleCarriere.libelle AS libelleCarriere, ")
                    .append("prop.codeRegroupement AS codeRegroupement, ")
                    .append("positionAdmin.code AS codePositionAdmin, ")
                    .append("positionAdmin.libelle AS libellePositionAdmin, ")
                    .append("prop.dateEntreeEchelon AS dateEntreeEchelon, ")
                    .append("prop.dateEntreeGrade AS dateEntreeGrade, ")
                    .append("prop.dateMiniAvancement AS dateMiniAvancement, ")
                    .append("prop.reliquat AS reliquat, ")
                    .append("prop.promouvable AS promouvable, ")
                    .append("prop.typeProposition AS typeProposition, ")
                    .append("prop.dateCalcul AS dateCalcul, ")
                    .append("prop.dateEffet AS dateEffet, ")
                    .append("prop.dateInjection AS dateInjection, ")
                    .append("prop.dateNomination AS dateNomination,")
                    .append("prop.dateRefus AS dateRefus,")
                    .append("prop.dateRetenue AS dateRetenue, ")
                    .append("prop.decision AS decision, ")
                    .append("prop.dureeRetardAnnee AS dureeRetardAnnee, ")
                    .append("prop.dureeRetardJour AS dureeRetardJour, ")
                    .append("prop.exclueManuellement AS exclueManuellement, ")
                    .append("prop.injection AS injection, ")
                    .append("prop.modeCreation AS modeCreation, ")
                    .append("prop.motifExclusion AS motifExclusion, ")
                    .append("prop.motifRefus AS motifRefus, ")
                    .append("prop.reclassementMedical AS reclassementMedical, ")
                    .append("prop.refuse AS refuse, ")
                    .append("prop.typeAvancement AS typeAvancement, ")
                    .append("prop.verifie AS verifie, ")
                    .append("gradeCible.code AS codeGradeCible, ")
                    .append("gradeCible.libelleMoyen AS libelleMoyenGradeCible, ")
                    .append("gradeCible.libelleLong AS libelleLongGradeCible , ")
                    .append("chevronCible.code AS codeChevronCible, ")
                    .append("prop.indiceBrut as indiceBrutProposition, ")
                	.append("gradeCible.cadreStatutaire.id AS idCadreStatutaire, ")
                	.append("prop.ancienneteAA AS ancienneteAA, ")
                    .append("prop.ancienneteMM AS ancienneteMM, ")
                    .append("prop.ancienneteJJ AS ancienneteJJ, ")
                    .append("motifAvancement.code AS codeMotifAvancement, ")
                    .append("motifAvancement.libelle AS libelleMotifAvancement, ")
                    .append("statutAvancement.code  AS statutAvancementCode, ")
                    .append("statutAvancement.libelle AS statutAvancementLibelle, ")
                   
                    // rangClassementAG Soit saisieLibre soit valeur (afficher la non nul)
                    .append("rang.valeur AS rangValeur, ")
                    .append("rang.saisieLibre AS rangSaisieLibre, ")
                    .append("concoursAgent.dateObtention AS dateObtentionConcours, ")
                    
                    // conditionDePropositionSelectionnee Sélection : oui si condition selectionnée = condition de la ligne mettre oui
                    .append("prop.conditionDePropositionSelectionnee.id AS idConditionSelectionne, ")
                    .append("prop.msgControle AS msgControle, ")
                    .append("tableauAG.dateSession AS dateCAP, ")
                    .append("tableauAG.code AS codeTableauAvancement, ")
                    .append("tableauAG.libelle AS libelleTableauAvancement, ")
                    .append("tableauAG.etatTableau AS phaseTableauAvancement, ")
                   
                    // Les concours
                    .append("concoursAgent.commentaire AS reussiteConcoursCommentaire, ")
                    // GIFT IN482 - STH - 07/04/2009 - Suppression des champs commission et du lien propositionAG
                    .append("concoursAgent.dateObtention AS reussiteConcoursDateObtention, ")
                    .append("concoursAgent.justificatifsFournis AS reussiteConcoursJustificatifsFournis, ")
                    .append("concoursAgent.typeConcours AS reussiteConcoursTypeConcours, ")
                    .append("concours.libelle AS concoursLibelle, ")
                    .append("gradeConcours.libelleLong AS concoursLibelleGrade ")
                    //36103 : ajout des dates de calcul et entrée dans la collect
                    .append(",(select max(grille.dateCalcul) from GrilleAnciennete as grille where grille.carriere = prop.carriere and grille.codeRegroupement = prop.codeRegroupement) AS dateCalculGrilleAnciennete ")
                    .append(" , COALESCE(")
                    .append(" (SELECT MIN(fad.dateDebut) FROM fad WHERE fad.agent.id=agent.id ")
                    .append(" AND fad.collectivite=car.collectivite")
                    .append(" AND TRUNC(fad.dateDebut) IN (SELECT DISTINCT TRUNC(fadmin.dateFin+1) FROM fad as fadmin WHERE fadmin.agent.id=agent.id)) ,")
                    .append(" (SELECT MAX(fad.dateDebut) FROM fad as fadmax WHERE fadmax.agent.id=agent.id ")
                    .append(")) as dateEntreeAgentCollect ")
                    // ESRH-6729
                    .append(", car.id as carriereId ")
                   
                    .append("FROM PropositionAG AS prop ")
                    .append("INNER JOIN prop.statutActuel AS statut ")
                    .append("INNER JOIN prop.carriere AS car ")
                    .append("INNER JOIN car.collectivite AS collectivite ")
                    .append("INNER JOIN car.agent AS agent ")
//                    .append("left join agent.listAgentUniteGestion as agtUnitGes ")
//                    .append("left join agtUnitGes.uniteGestion as uniteGestion ")
//                    .append("left join agtUnitGes.module as module with (module.code = :moduleCode)")
                    .append("LEFT JOIN agent.listFicheArriveeDepart as fad ")
                    .append("INNER JOIN car.libelleCarriere AS libelleCarriere ")
                    .append("INNER JOIN prop.fichePosition AS fichePosAdmin ")
                    .append("INNER JOIN fichePosAdmin.positionAdmin AS positionAdmin ")
                    .append("LEFT JOIN prop.gradeAncien AS gradeAncien ")
                    .append("LEFT JOIN prop.gradeCible AS gradeCible ")
                    .append("LEFT JOIN prop.chevronAncien AS chevAnc ")
                    .append("LEFT JOIN prop.chevronCible AS chevronCible ")
                    .append("LEFT JOIN prop.rangClassementAG AS rang ")
                    .append("LEFT JOIN prop.tableauAG AS tableauAG ")
                    .append("LEFT JOIN prop.motifAvancement AS motifAvancement ")
                    .append("LEFT JOIN prop.statutAvancement AS statutAvancement ")
                    .append("LEFT JOIN car.listFicheAffectation AS ficAffect WITH (ficAffect.dateDebut <= :date AND NVL(ficAffect.dateFin, (:tomorrow)) >= (:tomorrow)) ")
                    .append("LEFT JOIN ficAffect.service AS service ")
                    .append("LEFT JOIN car.listFichePoste AS ficPoste WITH (ficPoste.dateDebut <= :date AND NVL(ficPoste.dateFin, (:tomorrow)) >= (:tomorrow)) ")
                    .append("LEFT JOIN ficPoste.poste AS poste ")
                    .append("LEFT JOIN poste.listDescriptifPoste AS descrPoste WITH (descrPoste.dateDebut <= :date AND NVL(descrPoste.dateFin, (:tomorrow)) >= (:tomorrow)) ")
                    .append("LEFT JOIN prop.reussiteConcours AS concoursAgent ")
                    .append("LEFT JOIN concoursAgent.concours AS concours ")
                    .append("LEFT JOIN concours.gradeCible AS gradeConcours, ")
                    .append("HistoGrade AS gradeHist ")
                    .append("INNER JOIN gradeHist.grade AS grade  ")
                    .append("INNER JOIN gradeHist.categorie AS categorie  ")
                    .append("LEFT JOIN gradeHist.groupeHierarchique AS groupeHierarchique,  ")
                    .append("HistoCadreEmploi AS cadEmpHist ")
                    .append("INNER JOIN cadEmpHist.cadreEmploi AS cadEmp ")
                    .append("INNER JOIN cadEmpHist.filiere AS filiere ")

                    .append("WHERE gradeHist.cadreEmploi = cadEmp ")
                    .append("AND gradeCible = grade ")
                    .append("AND gradeHist.dateDebut <= :date ")
                    .append("AND NVL(gradeHist.dateFin, (:tomorrow)) >= (:tomorrow) ")
                    .append("AND cadEmpHist.cadreEmploi = cadEmp ")
                    .append("AND cadEmpHist.dateDebut <= :date ")
                    .append("AND NVL(cadEmpHist.dateFin, (:tomorrow)) >= (:tomorrow) "); 
                    //.append("and (module.code is null or module.code = :moduleCode) ");
            
//            if (listIdPropDejaRecup != null && listIdPropDejaRecup.size() > 0) {
//            	if(listIdPropDejaRecup.size()>=1000){
//            		//AMA mantis 0017027 tableau trop gros découpage de la liste
//            		float nbListe = UtilsNumberGRH.getArrondi((listIdPropDejaRecup.size()/1000),0);
//            		for(int i=0;i<=nbListe;i++){                    
//            			hql.append("and prop.id not in (:listIdPropDejaRecup"+i+") ");
//            		}
//            	}else{
//            		hql.append("and prop.id not in (:listIdPropDejaRecup) ");
//            	}                    
//            }

            Integer tableauAGId = (Integer) criteres.get("tableauAGId");
            if (tableauAGId != null) {
                hql.append("AND tableauAG.id = :tableauAGId ");
            } else {
                hql.append("AND tableauAG IS NULL ");
            }

            Boolean promouvable = (Boolean) criteres.get("promouvable");
            Boolean nonPromouvable = (Boolean) criteres.get("nonPromouvable");
            Boolean promu = (Boolean) criteres.get("promuExport");
            if (promouvable != null && nonPromouvable != null && promu !=null) {
                // avec les promus
                if(promu == Boolean.TRUE){
                    // promus ou promouvables
                    if (promouvable == Boolean.TRUE && nonPromouvable == Boolean.FALSE) {
                        hql.append("AND (prop.promouvable = true OR prop.promu = true )");
                    }
                    // promus ou non promouvables
                    else if (promouvable == Boolean.FALSE && nonPromouvable == Boolean.TRUE) {
                        hql.append("AND (prop.promouvable = false OR prop.promu = true ) ");
                    }  
                    // promus ou promouvables ou non promouvables
                    else if (promouvable == Boolean.TRUE && nonPromouvable == Boolean.TRUE) {
                       // hql.append(" AND (prop.promouvable = false OR prop.promouvable = true OR prop.promu = true) ");
                    }
                    // que les promus
                    else if(promouvable == Boolean.FALSE && nonPromouvable == Boolean.FALSE) {
                        hql.append(" AND prop.promu = true ");
                    }
                }
                // sans les promus
                else{
                    // sans les promus et que les promouvables
                    if (promouvable == Boolean.TRUE && nonPromouvable == Boolean.FALSE) {
                        hql.append("AND prop.promouvable = true AND prop.promu = false");
                    }
                    // sans les promus et que les non promouvables
                    else if (promouvable == Boolean.FALSE && nonPromouvable == Boolean.TRUE) {
                        hql.append("AND prop.promouvable = false ");
                    }
                    // sans promus et que les non promouvables et les promouvables
                    else if(promouvable == Boolean.TRUE && nonPromouvable == Boolean.TRUE){
                        hql.append("AND prop.promu = false ");
                    }
                }
            }

            // filtre pour les concours
            Boolean withConcoursAgent = criteres.containsKey("withConcoursAgent");
            Boolean withConcours = criteres.containsKey("withConcours");
            if (withConcoursAgent) {
                hql.append("AND concoursAgent IS NOT NULL ");
            }
            if (withConcours) {
                hql.append("AND concours IS NOT NULL ");
            }

            String gradeId = (String) criteres.get("gradeId");
            if (gradeId != null) {
                hql.append("AND gradeCible.id = :gradeId ");
            }

            String filiereId = (String) criteres.get("filiereId");
            if (filiereId != null) {
                hql.append("AND filiere.id = :filiereId ");
            }

            String cadreEmploiId = (String) criteres.get("cadreEmploiId");
            if (cadreEmploiId != null) {
                hql.append("AND cadEmp.id = :cadreEmploiId ");
            }

            EnumTypePropositionAG enumTypePropositionAG = (EnumTypePropositionAG) criteres.get("enumTypePropositionAG");
            if (enumTypePropositionAG != null) {
                hql.append("AND prop.typeProposition = :enumTypePropositionAG ");
            }

            EnumVerifierAG enumVerifier = (EnumVerifierAG) criteres.get("enumVerifier");
            if (enumVerifier != null) {
                hql.append("AND prop.verifie = :enumVerifier ");
            }

            String dateDebutObtention = (String) criteres.get("dateDebutObtention");
            if (dateDebutObtention != null) {
                hql.append("AND concoursAgent.dateObtention >= :dateDebutObtention ");
            }

            String dateFinObtention = (String) criteres.get("dateFinObtention");
            if (dateFinObtention != null) {
                hql.append("AND concoursAgent.dateObtention <= :dateFinObtention ");
            }

            EnumDecisionCAP decision = (EnumDecisionCAP) criteres.get("EnumDecisionCAP");
            if (decision != null) {
                hql.append("AND prop.decision = :decision ");
            }
            // fin filtre pour les concours

            String collectiviteId = (String) criteres.get("collectiviteId");
            if (collectiviteId != null) {
                hql.append("AND car.collectivite.id = :collectiviteId ");
            }
            query = getSession().createQuery(hql.toString());
            
            // MANTIS 35489 SROM 01/2017 : on borne la date de réference aux dates du tableau s'il existe + on supprime la notion de tomorrow qui est inexacte : il faut une seule date de réference
            Date date = getDateRefFromTableauAG(tableauAGId);
            //Date tomorrow = UtilsDate.addDays(date, 1);
            query.setDate("date", date);
            query.setDate("tomorrow", date);
            //query.setDate("tomorrow", tomorrow);
            
            if (tableauAGId != null)
                query.setInteger("tableauAGId", tableauAGId);

            if (collectiviteId != null)
                query.setString("collectiviteId", collectiviteId);

//            if (listIdPropDejaRecup != null && listIdPropDejaRecup.size() > 0) {
//            	if( listIdPropDejaRecup.size()>1000){
//            		//AMA mantis 0017027 tableau trop gros découpage de la liste
//            		float nbListe = UtilsNumberGRH.getArrondi((listIdPropDejaRecup.size()/1000),0);
//
//            		for(int i=0;i<=nbListe;i++){                
//            			log.debug("découpage de la liste nom param = listIdPropDejaRecup" +i +" sous liste allant de " +i*1000 + " a " +   Math.min(listIdPropDejaRecup.size()-1,((i+1)*1000)-1));
//            			query.setParameterList("listIdPropDejaRecup"+i, 
//            					listIdPropDejaRecup.subList(i*1000-(i==0?0:1),
//            							Math.min(listIdPropDejaRecup.size(),((i+1)*1000)-1)
//            					));
//            		}
//            	}else{
//            		query.setParameterList("listIdPropDejaRecup", listIdPropDejaRecup);
//            	}
//
//            }
//            log.debug("découpage terminé ");            // Application des filtres sur concours
            try {
                if (dateDebutObtention != null) {
                	query.setDate("dateDebutObtention", UtilsDate.Formatage.DATE_FORMATTER.parse(dateDebutObtention));
                }
                if (dateFinObtention != null) {
                	query.setDate("dateFinObtention", UtilsDate.Formatage.DATE_FORMATTER.parse(dateFinObtention));
                }

            } catch (ParseException e) {
                throw new TechnicalException(TechnicalException.Type.UNEXPECTED_EXCEPTION, e);
            }

            if (gradeId != null) {
            	query.setString("gradeId", gradeId);
            }
            if (cadreEmploiId != null) {
            	query.setString("cadreEmploiId", cadreEmploiId);
            }
            if (filiereId != null) {
            	query.setString("filiereId", filiereId);
            }
            if (decision != null) {
            	query.setParameter("decision", decision);
            }

            query.setResultTransformer(Transformers.aliasToBean(PropositionAGAExporterDTO.class));
            
        try {
        	
        	List<PropositionAGAExporterDTO> list = query.list();
            
            // MANTIS 27107 SROM 02/2015 : Traitement java des prop not in (listIdPropDejaRecup)
            if (listIdPropDejaRecup != null && listIdPropDejaRecup.size() > 0) {
            	List<PropositionAGAExporterDTO> listASuppr = new ArrayList<PropositionAGAExporterDTO>();
            	for (PropositionAGAExporterDTO prop : list) {
            		if (listIdPropDejaRecup.contains(prop.getId())) {
            			listASuppr.add(prop);
            		}
            	}
            	if (listASuppr.size() > 0) {
            		list.removeAll(listASuppr);
            	}
            }
            
            int size = list.size();
            for (int i = 0; i < size; i++) {
                PropositionAGAExporterDTO prop = list.get(i);
                List<AvisAGDTO> listAvis = findListAvisAGDTOByPropositionId(prop.getId());
                if (listAvis != null && !listAvis.isEmpty()) {
                    prop.setListAvisAGDTO(listAvis);
                }             
            }
            for (int i = 0; i < size; i++) {
                // renseignement des valeurs et saisie libre de rang de classement s'il y en a un lier a la proposition
                PropositionAGAExporterDTO prop = list.get(i);
                // renseignement des conditions s'il y en a un lier a la proposition
                List<ConditionDeProposition> listConditionRemplie = findListConditionDePropositionRemplieByPropositionId(prop.getId());
                prop.setListConditionAGRemplie(listConditionRemplie);
            }
            return list;
        } catch (HibernateException hibExc) {
            throw new DaoException(hibExc);
        }
        } catch (Exception e) {
            log.debug(e.getMessage());
            return null;
        } 
        
        
    }    
    
    /*------------------------------------------------------------------------------	
     * 
     * FIN :  export des propositions 
     
     * DEBUT : Detail Avancement Verification PropositionAg
     * 
     -------------------------------------------------------------------------------*/
    /**
     * @see IDaoPropositionAG#calculListDetailAvancementVerificationPropositionAg(Integer) DetailAvancement n'a pas de
     *      representation en BD, il est construit a partir d'element de la propositionAG et de calcul fait a partir de
     *      celle ci
     */
    @Override
	@SuppressWarnings("unchecked")
    @Transactional(propagation = Propagation.SUPPORTS, readOnly = true)
    public List<DetailAvancementAGDTO> calculListDetailAvancementVerificationPropositionAg(Integer tableauId) {
        try {
            String fromWhere = " from PropositionAG as prop " 
                    + " inner join prop.gradeCible as grade, "
                    + " HistoGrade histoGrade,  HistoCadreEmploi histoCadre  "
                    + " inner join histoCadre.cadreEmploi as cadreEmploi  " 
                    + " where prop.tableauAG.id= :tableauid "
                    + " and histoGrade.grade = grade  " 
                    + " and histoGrade.cadreEmploi = cadreEmploi "
                    + " and histoGrade.dateDebut <= :date "
                    + " and (histoGrade.dateFin is NULL or histoGrade.dateFin >= :date)  "
                    + " and histoCadre.cadreEmploi = histoGrade.cadreEmploi  " 
                    + " and histoCadre.dateDebut <= :date  "
                    + " and (histoCadre.dateFin is NULL or histoCadre.dateFin >= :date) ";

            // Confidentialité
            HqlSecure hqls = getSecurityFilter().secureClause(SecurityMgrGRH.CONFIDENTIALITE_AGENT, "prop.carriere.agent");
            if (hqls != null && hqls.where.length() > 0) {
                fromWhere += (" and") + hqls.where;
            }

            /*
             * En phase de vérification des propositions, dans le détail de la progression, il faut ajouter le grade

Remplacer les deux colonnes "Filière" et "Cadre d'emploi" par la colonne "Grade", afficher le libellé moyen du grade.
Trier par Filière, Cadre d'emploi et Grade

Tri sur filière : Filière.codeTri + Filière.code
Tri sur cadre d'emploi : CadreEmploi.codeTri + CadreEmploi.code
Tri sur grade : Grade.codeTri + Grade.code 
             */
            Query queryCadreEmploi = getSession().createQuery(
                   // "select histoCadre.filiere.libelle as filiereLibelle,"
                   // + " cadreEmploi.libelle as cadreEmploiLibelle," 
                   // + " cadreEmploi.id as cadreEmploiId, "
                    "select histoCadre.filiere.codeTri as filiereCodeTri, "
                    + " histoCadre.filiere.code as filiereCode, "
                    + " cadreEmploi.codeTri as cadreEmploiCodeTri, "
                    + " cadreEmploi.code as cadreEmploiCode, "
                    + " grade.codeTri as gradeCodeTri, "
                    + " grade.code as gradeCode, "
                    + " grade.libelleMoyen as gradeLibelle, "
                    + " grade.id as gradeId "
                    + fromWhere 
                   //+ " group by histoCadre.filiere.libelle,cadreEmploi.libelle ,cadreEmploi.id  "
                   //+ " order by histoCadre.filiere.libelle,cadreEmploi.libelle ,cadreEmploi.id");
                    + " group by histoCadre.filiere.codeTri,histoCadre.filiere.code , cadreEmploi.codeTri, cadreEmploi.code, grade.codeTri, grade.code, grade.libelleMoyen, grade.id  "
                    + " order by histoCadre.filiere.codeTri,histoCadre.filiere.code , cadreEmploi.codeTri, cadreEmploi.code, grade.codeTri, grade.code, grade.libelleMoyen, grade.id  ");
                    
            queryCadreEmploi.setInteger("tableauid", tableauId);
            queryCadreEmploi.setDate("date", new Date());
            if (hqls != null && hqls.criteriaValues != null) {
                queryCadreEmploi.setProperties(hqls.criteriaValues);
            }

            queryCadreEmploi.setResultTransformer(Transformers.aliasToBean(DetailAvancementAGDTO.class));
            // if (firstLine > -1) query.setFirstResult((int) firstLine);
            // if (limitLine > -1) query.setMaxResults((int) limitLine );
           	List<DetailAvancementAGDTO> listDetail = queryCadreEmploi.list();
            for (int i = 0; i < listDetail.size(); i++) {
                DetailAvancementAGDTO detail = listDetail.get(i);
                // detail.setTableauAG(tableau);
                // calcul des totaux
                Query query = getSession().createQuery(
                        "select count(*) " 
                        + fromWhere 
                        + " and prop.promouvable = :promouvable "
                        + " and grade.id =:idGrade ");

                query.setInteger("tableauid", tableauId);
                query.setDate("date", new Date());
                query.setBoolean("promouvable", true);
                query.setString("idGrade", detail.getGradeId());
                if (hqls != null && hqls.criteriaValues != null) {
                    query.setProperties(hqls.criteriaValues);
                }

                detail.setNbPromouvableTotal(((Long) query.uniqueResult()).intValue());

                query.setBoolean("promouvable", false);
                detail.setNbNonPromouvableTotal(((Long) query.uniqueResult()).intValue());

                // calcul des details promouvable
                query = getSession().createQuery(
                        "select count(*) " 
                        + fromWhere 
                        + " and prop.promouvable = :promouvable "
                        + " and grade.id =:idGrade " 
                        + " and prop.verifie = :verifiee");

                query.setInteger("tableauid", tableauId);
                query.setDate("date", new Date());
                query.setString("idGrade", detail.getGradeId());

                query.setBoolean("promouvable", true);
                query.setParameter("verifiee", EnumVerifierAG.AVERIFIER);

                if (hqls != null && hqls.criteriaValues != null) {
                    query.setProperties(hqls.criteriaValues);
                }

                detail.setNbPromouvableAVerifiee(((Long) query.uniqueResult()).intValue());

                query.setParameter("verifiee", EnumVerifierAG.OUI);
                detail.setNbPromouvableVerifiee(((Long) query.uniqueResult()).intValue());

                // calcul des details non promouvable
                query.setBoolean("promouvable", false);
                query.setParameter("verifiee", EnumVerifierAG.AVERIFIER);
                detail.setNbNonPromouvableAVerifiee(((Long) query.uniqueResult()).intValue());

                query.setParameter("verifiee", EnumVerifierAG.OUI);
                detail.setNbNonPromouvableVerifiee(((Long) query.uniqueResult()).intValue());
            }
            return listDetail;
        } catch (HibernateException hibExc) {
            throw new DaoException(hibExc);
        }
    }

    /*------------------------------------------------------------------------------	
     * 
     * FIN :  Detail Avancement Verification PropositionAg 
     
     * DEBUT : injection 
     * 
     -------------------------------------------------------------------------------*/
    private final String whereNonInjecteeAndControleeNonInjectable = " where (prop.injection = '"
            + EnumTypeInjectionAG.NON_INJECTE.getCode()
            + "' "
            + " or (prop.injection ='"
            + EnumTypeInjectionAG.CONTROLEE_OK.getCode()
            + "' and prop.dateNomination is not null " 
            +" AND ((car.collectivite.periodePaie is not null " 
            +" AND length(trim(car.collectivite.periodePaie))>0 " 
            +" AND prop.dateNomination < add_months(to_date(car.collectivite.periodePaie, 'YYYY.MM'),1)) or (car.libelleCarriere.type = 'V') ) ) "
            + " or (prop.injection ='"
            + EnumTypeInjectionAG.CONTROLEE_KO.getCode()
            + "' and prop.dateNomination is not null " 
            +" AND ((car.collectivite.periodePaie is not null " 
            +" AND length(trim(car.collectivite.periodePaie))>0 " 
            +" AND prop.dateNomination < add_months(to_date(car.collectivite.periodePaie, 'YYYY.MM'),1)) or (car.libelleCarriere.type = 'V') ) ) ) "
            + " and histoGrade.grade = grade" + " and histoGrade.dateDebut <= :date"
            + " and (histoGrade.dateFin is null or histoGrade.dateFin > :date)" +

            " and histoCadre.cadreEmploi = histoGrade.cadreEmploi" + " and histoCadre.dateDebut <= :date"
            + " and (histoCadre.dateFin is null or histoCadre.dateFin > :date) ";
    
    private final String whereHisto = " where histoGrade.grade = grade" + " and histoGrade.dateDebut <= :date"
            + " and (histoGrade.dateFin is null or histoGrade.dateFin > :date)" +

            " and histoCadre.cadreEmploi = histoGrade.cadreEmploi" + " and histoCadre.dateDebut <= :date"
            + " and (histoCadre.dateFin is null or histoCadre.dateFin > :date) ";

    /**
     * @see IDaoPropositionAG#findPropositionAGInjection(Map, Map, long, long, String)
     * 
     */
    @Override
	@SuppressWarnings("unchecked")
    @Transactional(propagation = Propagation.SUPPORTS, readOnly = true)
    public List<PropositionAGDTO> findPropositionAGInjection(Map<String, Integer> tri, Map<String, Object> criteres,
            long firstLine, long limitLine, String filter) {
        try {
            String requete = null;
            EnumTypeInjectionAG enumTypeInjectionAG = (EnumTypeInjectionAG) criteres.get("enumTypeInjectionAG");
            if (enumTypeInjectionAG != null) {
                if (EnumTypeInjectionAG.NON_INJECTE.equals(enumTypeInjectionAG)) {
                    requete = requetePropositionAGDTO + fromPropoposition + ", HistoGrade as histoGrade"
                            + ", HistoCadreEmploi as histoCadre " + whereNonInjecteeAndControleeNonInjectable;
                }
                if (EnumTypeInjectionAG.REJETEE.equals(enumTypeInjectionAG)) {
                    requete = requetePropositionAGDTO + fromPropoposition + ", HistoGrade as histoGrade"
                            + ", HistoCadreEmploi as histoCadre " + whereHisto + " and prop.promouvable = true ";
                    criteres.put("injection", EnumTypeInjectionAG.REJETEE);
                }
                if (EnumTypeInjectionAG.CONTROLEE_KO.equals(enumTypeInjectionAG)
                        || EnumTypeInjectionAG.CONTROLEE_OK.equals(enumTypeInjectionAG)) {
                    requete = requetePropositionAGDTO + ", imprime.numeroArrete as numeroArrete, imprimespv.numeroArrete as numeroArreteSPV " + fromPropoposition + " left join prop.tableauAG as tableau left join prop.imprime as imprime left join prop.imprimeSPV as imprimespv, HistoGrade as histoGrade"
                            + ", HistoCadreEmploi as histoCadre " + whereHisto + " and prop.promouvable = true ";
                    criteres.put("isControlled", Boolean.TRUE);
                    criteres.put("isInjectable", Boolean.TRUE);
                }
                if (EnumTypeInjectionAG.INJECTE.equals(enumTypeInjectionAG)) {
                    requete = requetePropositionAGDTO
                            + " , prop.dateInjection as dateInjection, imprime.numeroArrete as numeroArrete, imprimespv.numeroArrete as numeroArreteSPV "
                            + fromPropoposition + " left join prop.tableauAG as tableau left join prop.imprime as imprime left join prop.imprimeSPV as imprimespv , HistoGrade as histoGrade "
                            + ", HistoCadreEmploi as histoCadre " + whereHisto + " and prop.promouvable = true ";
                    criteres.put("injection", EnumTypeInjectionAG.INJECTE);
                }
            } else {
                requete = requetePropositionAGDTO
                        + " , prop.dateInjection as dateInjection, imprime.numeroArrete as numeroArrete, imprimespv.numeroArrete as numeroArreteSPV "
                        + fromPropoposition + " left join prop.imprime as imprime left join prop.imprimeSPV as imprimespv , HistoGrade as histoGrade "
                        + ", HistoCadreEmploi as histoCadre " + whereHisto;
            }
            if (requete == null)
                return new ArrayList<PropositionAGDTO>(0);

            Query query = prepareQueryListPropositions(tri, criteres, requete, null, true);
            if (query == null)
                return new ArrayList<PropositionAGDTO>(0);
            this.applyLimits(filter, query, (int)firstLine, (int)limitLine);
            query.setResultTransformer(Transformers.aliasToBean(PropositionAGDTO.class));
            List<PropositionAGDTO> list = (List<PropositionAGDTO>) this.filterList(filter, query.list());
            for(PropositionAGDTO prop : list){
            	//***** Libelle grade SPV ***/
                if(prop.getDateGradeSPV() != null){
                	Query queryGradeSPV  = getSession().createQuery("SELECT prop.gradeSPVActuel, prop.gradeSPVFutur FROM PropositionAG prop WHERE prop.id = :idProp");
                	queryGradeSPV.setInteger("idProp", prop.getId());
                	Object[] result = (Object[])queryGradeSPV.uniqueResult();
                	if(result.length == 2){
                		if(result[0] != null){
                			prop.setLibelleGradeSPV(((Grade)result[0]).getLibelleMoyen());
                		}
                		if(result[1]!=null){
                			prop.setLibelleGradeSPVFutur(((Grade)result[1]).getLibelleMoyen());
                		}
                	}
                }
                
                //***** Type de carrière ***/
                Query queryTypCar  = getSession().createQuery(
                		"SELECT libcar.type " +
                		"FROM PropositionAG prop " +
                		"LEFT JOIN prop.carriere car " +
                		"LEFT JOIN car.libelleCarriere libcar " +
                		"WHERE prop.id = :idProp");
                queryTypCar.setInteger("idProp", prop.getId());
            	String result = (String) queryTypCar.uniqueResult();
            	if(result != null && "V".equals(result)){
            		prop.setCarriereSPV(true);
            	}
            }
            return list;
        } catch (HibernateException hibExc) {
            throw new DaoException(hibExc);
        }
    }

    /**
     * @see IDaoPropositionAG#countPropositionAGInjection(Map, String)
     * 
     */
    @Override
	@Transactional(propagation = Propagation.SUPPORTS, readOnly = true)
    public Long countPropositionAGInjection(Map<String, Object> criteria, String filter) {
        try {
            String requete = null;
            EnumTypeInjectionAG enumTypeInjectionAG = (EnumTypeInjectionAG) criteria.get("enumTypeInjectionAG");
            if (enumTypeInjectionAG != null) {
                if (EnumTypeInjectionAG.NON_INJECTE.equals(enumTypeInjectionAG)) {
                    requete = "select count(*) " + fromPropoposition + ", HistoGrade as histoGrade"
                            + ", HistoCadreEmploi as histoCadre " + whereNonInjecteeAndControleeNonInjectable;
                }
                if (EnumTypeInjectionAG.REJETEE.equals(enumTypeInjectionAG)) {
                    requete = "select count(*) " + fromPropoposition + ", HistoGrade as histoGrade"
                            + ", HistoCadreEmploi as histoCadre " + whereHisto + " and prop.promouvable = true ";
                    criteria.put("injection", EnumTypeInjectionAG.REJETEE);
                }
                if (EnumTypeInjectionAG.CONTROLEE_KO.equals(enumTypeInjectionAG)
                        || EnumTypeInjectionAG.CONTROLEE_OK.equals(enumTypeInjectionAG)) {
                    requete = "select count(*) " + fromPropoposition + ", HistoGrade as histoGrade"
                            + ", HistoCadreEmploi as histoCadre " + whereHisto + " and prop.promouvable = true ";
                    criteria.put("isControlled", Boolean.TRUE);
                    criteria.put("isInjectable", Boolean.TRUE);
                }
                if (EnumTypeInjectionAG.INJECTE.equals(enumTypeInjectionAG)) {
                    requete = "select count(*) " + fromPropoposition + " left join prop.tableauAG as tableau, HistoGrade as histoGrade"
                            + ", HistoCadreEmploi as histoCadre " + whereHisto + " and prop.promouvable = true ";
                    criteria.put("injection", EnumTypeInjectionAG.INJECTE);
                }
            } else {
                requete = "select count(*) " + fromPropoposition + ", HistoGrade as histoGrade"
                        + ", HistoCadreEmploi as histoCadre " + whereHisto;
            }
            if (requete == null)
                return Long.valueOf(0);
            Query query = prepareQueryListPropositions(null, criteria, requete, null, true);
            return (Long) query.uniqueResult();
        } catch (HibernateException hibExc) {
            throw new DaoException(hibExc);
        }
    }

    /**
     * @see IDaoPropositionAG#refuserPromotion(Integer, Date, String))
     */
    @Override
	@Transactional(propagation = Propagation.REQUIRED)
    public void refuserPromotion(Integer propositionId, Date dateRefus, String motifRefus) {
        try {
            if (propositionId == null)
                return;
            Query query = getSession().createQuery(
                    "update from PropositionAG prop set prop.refuse = :refuse, prop.dateRefus = :dateRefus, prop.motifRefus =:motifRefus where prop.id = :propositionId");
            query.setInteger("propositionId", propositionId);
            query.setDate("dateRefus", dateRefus);
            query.setString("motifRefus", motifRefus);
            query.setBoolean("refuse", true);
            query.executeUpdate();
        } catch (HibernateException hibEx) {
            throw new DaoException(hibEx);
        }
    }

    /**
     * @see IDaoPropositionAG#excluePropositionManuellement(Integer, String)))
     */
    @Override
	@Transactional(propagation = Propagation.REQUIRED)
    public void excluePropositionManuellement(Integer propositionId, String motif) {
        try {
            if (propositionId == null)
                return;
            Query query = getSession().createQuery(
                    "update from PropositionAG prop set prop.motifExclusion = :motif, prop.exclueManuellement=:exclue where prop.id = :propositionId");
            query.setInteger("propositionId", propositionId);
            query.setString("motif", motif);
            query.setBoolean("exclue", true);
            query.executeUpdate();
        } catch (HibernateException hibEx) {
            throw new DaoException(hibEx);
        }
    }

    @Override
	@Transactional(propagation = Propagation.SUPPORTS, readOnly = true)
    public PropositionAG findPropositonAGAnneePrecedente(Date dateLimite, Carriere carriere, Long codeRegroupement) {
        try {
            Date dateDebutAnnee = (dateLimite == null ? new Date() : dateLimite);

            Calendar calDebutAnnee = Calendar.getInstance();
            calDebutAnnee.setTime(dateDebutAnnee);
            calDebutAnnee.set(Calendar.DATE, 1);
            calDebutAnnee.set(Calendar.MONTH, Calendar.JANUARY);
            dateDebutAnnee = calDebutAnnee.getTime();

            // TODO on ne devrait pas avoir besoin du setMaxResults(1) ?
            Criteria crit = getSession().createCriteria(PropositionAG.class)
					.add(Restrictions.eq("carriere", carriere))
					.add(Restrictions.eq("codeRegroupement", codeRegroupement))
					.add(Restrictions.lt("dateCalcul", dateDebutAnnee))
					.addOrder(Order.desc("dateCalcul"))
					.setMaxResults(1);

            return (PropositionAG) crit.uniqueResult();
        } catch (HibernateException hibExc) {
            throw new DaoException(TypeDaoException.FATAL, hibExc);
        }
    }

    /**
     * @see IDaoPropositionAG#loadByTableauAndAgent(TableauAG, Agent)
     */
    @Override
	@SuppressWarnings("unchecked")
    @Transactional(propagation = Propagation.SUPPORTS, readOnly = true)
    public List<PropositionAG> loadByTableauAndAgent(TableauAG tableauAG, Agent agent) {
        try {
            Query query = getSession().createQuery(
                    "from PropositionAG as prop" + " where prop.tableauAG = :tableauAG"
                            + " and prop.carriere.agent = :agent");
            query.setEntity("agent", agent);
            query.setEntity("tableauAG", tableauAG);
            return query.list();
        } catch (HibernateException hibExc) {
            throw new DaoException(TypeDaoException.FATAL, hibExc);
        }
    }

    /**
     * @see IDaoPropositionAG#loadByTableauAndAgent(TableauAG, Agent)
     */
    @Override
	@SuppressWarnings("unchecked")
    @Transactional(propagation = Propagation.SUPPORTS, readOnly = true)
    public List<PropositionAG> loadByListTableauAndCarriere(List<Integer> listTableauAG, Carriere carriere) {
        try {
        	StringBuffer sb = new StringBuffer( "from PropositionAG as prop where prop.carriere = :carriere");
        	if(!CollectionUtils.isEmpty(listTableauAG)) {
        		sb.append(" and prop.tableauAG.id in (:listTableauAG)");
        	}
            Query query = getSession().createQuery(sb.toString());
            
            query.setEntity("carriere", carriere);
            if(!CollectionUtils.isEmpty(listTableauAG)) {
            	query.setParameterList("listTableauAG", listTableauAG);
        	}
            
            return query.list();
        } catch (HibernateException hibExc) {
            throw new DaoException(TypeDaoException.FATAL, hibExc);
        }
    }
    
    @Override
	@SuppressWarnings("unchecked")
    @Transactional(propagation = Propagation.SUPPORTS, readOnly = true)
    public List<Integer> findListPropositionAGIdsByCollectivitesForControl(List<String> idCollectivites) {
        try {
            StringBuilder hql = new StringBuilder(300);
            hql.append("select prop.id from PropositionAG as prop"
                    + " inner join prop.carriere as car"
                    + " left join prop.tableauAG as tableau with tableau.etatTableau > :phaseTableauTRAITEMENT_AVANCEMENTS"
                    + " left join car.collectivite as col "
                    + " left join car.libelleCarriere as libCar "
                    + " left join car.agent as agt "
                    + " where prop.promu = :promu and col.id in (:listIdCollectivites)"
                    + " and prop.dateInjection is null" + " and prop.modeCreation != :modeCreationSIMULATION"
                    + " and (TO_CHAR( prop.dateNomination, 'YYYY.MM' ) <= col.periodePaie"
                    + " OR libCar.type = 'V' )");

            // Confidentialité
            HqlSecure hqls = getSecurityFilter().secureClause(SecurityMgrGRH.CONFIDENTIALITE_AGENT, "agt");
            if (hqls != null && hqls.where.length() > 0) {
                hql.append(" and").append(hqls.where);
            }

            hql.append(" order by col.code ");

            Query query = getSession().createQuery(hql.toString());
            // Mantis DA 5620 - LDEC - 01/06/2010 - Le lancement concurrent de plusieurs controle injection fesait planté tout les lancement concurrent sauf 1. 
            //query.setLockMode("prop", LockMode.UPGRADE);
            query.setParameterList("listIdCollectivites", idCollectivites);
            query.setBoolean("promu", Boolean.TRUE);
            query.setParameter("phaseTableauTRAITEMENT_AVANCEMENTS", EnumPhaseTableauAG.TRAITEMENT_AVANCEMENTS);
            query.setParameter("modeCreationSIMULATION", EnumModeCreation.SIMULATION);
            if (hqls != null && hqls.criteriaValues != null) {
                query.setProperties(hqls.criteriaValues);
            }
            return query.list();
        } catch (HibernateException hibExc) {
            throw new DaoException(getSession(), hibExc);
        }
    }

    /**
     * @see IDaoPropositionAG#findListSimulePropositionId(String, Long)
     */
    @Override
	@SuppressWarnings("unchecked")
    @Transactional(propagation = Propagation.SUPPORTS, readOnly = true)
    public List<Integer> findListSimulePropositionId(String carriereId, Long codeRegroupement) {
        try {
            Query query = getSession().createQuery(
                    "select prop.id from PropositionAG as prop" + " where prop.carriere.id = :carriereId"
                            + " and prop.codeRegroupement = :codeRegroupement"
                            + " and prop.typeProposition = :typeProposition" + " order by prop.id");
            query.setString("carriereId", carriereId);
            query.setLong("codeRegroupement", codeRegroupement);
            query.setParameter("typeProposition", EnumTypePropositionAG.SIMULATION);
            return query.list();
        } catch (HibernateException hibExc) {
            throw new DaoException(TypeDaoException.FATAL, hibExc);
        }
    }

    /**
     * @see IDaoPropositionAG#deletePropositionsByTableau(TableauAG)
     */
    @Override
	@Transactional(propagation = Propagation.REQUIRED)
    public void deletePropositionsByTableau(TableauAG tableauAG) {
        try {
            Query query = getSession().createQuery("delete PropositionAG p where p.tableauAG=:tableau");
            query.setEntity("tableau", tableauAG);
            query.executeUpdate();
        } catch (HibernateException hibExc) {
            throw new DaoException(TypeDaoException.FATAL, hibExc);
        }
    }

    /**
     * @see IDaoPropositionAG#deletePropositionsByTableauAGAndGradeCible(TableauAG,Grade)
     */
    @Override
	@Transactional(propagation = Propagation.REQUIRED)
    public void deletePropositionsByTableauAGAndGradeCible(TableauAG tableauAG, Grade gradeCible) {
        try {
            Query query = getSession().createQuery(
                    "delete PropositionAG p where p.tableauAG=:tableau and p.gradeCible =:gradeCible ");
            query.setEntity("tableau", tableauAG);
            query.setEntity("gradeCible", gradeCible);
            query.executeUpdate();
        } catch (HibernateException hibExc) {
            throw new DaoException(TypeDaoException.FATAL, hibExc);
        }
    }
    
    @Override
    @Transactional(propagation = Propagation.REQUIRED)
    public void deleteAllPropositionsSimulation() {
        try {
            Query query = getSession().createQuery("delete PropositionAG p where p.tableauAG is null ");
            query.executeUpdate();
        } catch (HibernateException hibExc) {
            throw new DaoException(TypeDaoException.FATAL, hibExc);
        }
    }

    /**
     * @see IDaoPropositionAG#findListPropositionsByLigneConditionAG( LigneConditionAG)
     */
    @Override
	@SuppressWarnings("unchecked")
    @Transactional(propagation = Propagation.SUPPORTS, readOnly=true)
    public List<PropositionAG> findListPropositionsByLigneConditionAG(LigneConditionAG ligneConditionAG) {
        try {
            String deleteRequete = 
            "select prop from ConditionDeProposition as condition " 
          + " left join  condition.propositionAG as prop "
          + " where condition.conditionAG.id = :idLignecondition " 
          + " and prop.tableauAG.etatTableau < :etatFermee";

            Query query = getSession().createQuery(deleteRequete);
            query.setInteger("idLignecondition", ligneConditionAG.getId());
            query.setString("etatFermee", EnumPhaseTableauAG.TABLEAU_CLOS.getCode());

            return query.list();
        } catch (HibernateException hibExc) {
            throw new DaoException(TypeDaoException.FATAL, hibExc);
        }
    }
    
    /**
     * @see IDaoPropositionAG#findListPropositionsByLigneConditionAGAfterDate( LigneConditionAG, Date)
     */
    @Override
    @SuppressWarnings("unchecked")
    @Transactional(propagation = Propagation.SUPPORTS, readOnly=true)
    public List<PropositionAG> findListPropositionsByLigneConditionAGAfterDate(LigneConditionAG ligneConditionAG, Date dateRef) {
        try {
            String deleteRequete = 
            "select prop from ConditionDeProposition as condition " 
          + " left join  condition.propositionAG as prop "
          + " left join  prop.tableauAG as tabAG "
          + " where condition.conditionAG.id = :idLignecondition " 
          + " and tabAG.etatTableau < :etatFermee"
          + " and coalesce(tabAG.dateFin,:infiniteDate) > :dateRef ";

            Query query = getSession().createQuery(deleteRequete);
            query.setInteger("idLignecondition", ligneConditionAG.getId());
            query.setString("etatFermee", EnumPhaseTableauAG.TABLEAU_CLOS.getCode());
            query.setDate("infiniteDate", UtilsDate.INFINITE_DATE_CS);
            query.setDate("dateRef", dateRef);
            return query.list();
        } catch (HibernateException hibExc) {
            throw new DaoException(TypeDaoException.FATAL, hibExc);
        }
    }
    
    /**
     * @see IDaoPropositionAG#findListPropositionsSimuleByLigneConditionAG( LigneConditionAG)
     */
    @Override
	@SuppressWarnings("unchecked")
    @Transactional(propagation = Propagation.SUPPORTS, readOnly=true)
    public List<PropositionAG> findListPropositionsSimuleByLigneConditionAG(LigneConditionAG ligneConditionAG) {
        try {
            String deleteRequete = 
            "select prop from ConditionDeProposition as condition " 
          + " left join  condition.propositionAG as prop "
          + " where condition.conditionAG.id = :idLignecondition " 
          + " and prop.typeProposition = :typeProposition";

            Query query = getSession().createQuery(deleteRequete);
            query.setInteger("idLignecondition", ligneConditionAG.getId());
            query.setParameter("typeProposition", EnumTypePropositionAG.SIMULATION);

            return query.list();
        } catch (HibernateException hibExc) {
            throw new DaoException(TypeDaoException.FATAL, hibExc);
        }
    }

    /**
     * @see IDaoPropositionAG#deleteAllPropositionSimule()
     */
    @Override
	@Transactional(propagation = Propagation.REQUIRED)
    public int deleteAllPropositionSimule(String carriereId, Long codeRegroupement, Grade gradeCible,
            Grade gradeSource, final List<EnumTypeAvancementAG> listTypeAvancement) {
        try {
            StringBuffer hql = new StringBuffer(300);
            hql.append("delete from PropositionAG as prop" + " where prop.carriere.id = :carriereId"
                    + "   and prop.codeRegroupement = :codeRegroupement"
                    + "   and prop.typeProposition = :typeProposition");

            if (gradeSource != null) {
                hql.append(" and prop.gradeAncien = :gradeSource");
            } else if (gradeCible != null) {
                hql.append(" and prop.gradeCible = :gradeCible");
            }
            if (listTypeAvancement != null && !listTypeAvancement.isEmpty()) {
                hql.append(" and prop.typeAvancement in (:listTypeAvancement)");
            }

            Query query = getSession().createQuery(hql.toString());
            query.setString("carriereId", carriereId);
            query.setLong("codeRegroupement", codeRegroupement);
            query.setParameter("typeProposition", EnumTypePropositionAG.SIMULATION);

            if (gradeSource != null) {
                query.setEntity("gradeSource", gradeSource);
            } else if (gradeCible != null) {
                query.setEntity("gradeCible", gradeCible);
            }
            if (listTypeAvancement != null && !listTypeAvancement.isEmpty()) {
                query.setParameterList("listTypeAvancement", listTypeAvancement);
            }

            return query.executeUpdate();
        } catch (HibernateException hibExc) {
            throw new DaoException(TypeDaoException.FATAL, hibExc);
        }
    }

    /**
     * @see IDaoPropositionAG#findListPropositionSimule(String, Long)
     */
    @Override
	@Transactional(propagation = Propagation.REQUIRED)
    public void deletePropositionSimuleByAgents(List<Agent> agents) {
        try {
            Query query = getSession().createQuery(
                    "delete from PropositionAG as prop"
                            + " where EXISTS (select car.id from Carriere car where car = prop.carriere and car.agent IN ( :listeAgent ))"
                            + " and prop.typeProposition = :typeProposition");
            query.setParameterList("listeAgent", agents);
            query.setParameter("typeProposition", EnumTypePropositionAG.SIMULATION);
            query.executeUpdate();
        } catch (HibernateException hibExc) {
            throw new DaoException(TypeDaoException.FATAL, hibExc);
        }
    }

    @Override
    @Transactional(propagation = Propagation.REQUIRED)
    public void deletePropositionAGByAgent(final List<Carriere> listCarriere) {
        try {
            if (listCarriere!=null && listCarriere.size()>0){
                StringBuilder hql = new StringBuilder(80);
                hql.append("DELETE PropositionAG prop ");
                hql.append("WHERE prop.carriere IN (:listCarriere)");
                Query query = getSession().createQuery(hql.toString());
                query.setParameterList("listCarriere", listCarriere);
                int nbSuppr = query.executeUpdate();
                log.debug("Suppression de " + nbSuppr + " propositions AG");
            }
        } catch (HibernateException hibExc) {
            log.error("Erreur Hibernate", hibExc);
            throw new DaoException(TypeDaoException.FATAL, hibExc);
        }
    }

    /**
     * @see IDaoPropositionAG#countPromotionsConcoursAgent(Map, String)
     */
    @Override
	@Transactional(propagation = Propagation.SUPPORTS, readOnly = true)
    public Long countPromotionsConcoursAgent(Map<String, Object> criteria, String filter) {
        try {
            String hql = "select count(*) " + fromPropopositionWithConcours + ", HistoGrade as histoGrade"
                    + ", HistoCadreEmploi as histoCadre " + whereHisto + " and prop.tableauAG is null ";
            Query query = prepareQueryListPropositions(null, criteria, hql, null, true);
            if (query == null)
                return null;

            return (Long) query.uniqueResult();
        } catch (HibernateException hibEx) {
            throw new DaoException(hibEx);
        }
    }

    /**
     * @see IDaoPropositionAG#findListPromotionsConcoursAgent(Map, Map, long, long, String)
     */
    @Override
	@SuppressWarnings("unchecked")
    @Transactional(propagation = Propagation.SUPPORTS, readOnly = true)
    public List<PropositionAG> findListPromotionsConcoursAgent(Map<String, Integer> tri, Map<String, Object> criteria,
            long firstLine, long limitLine, String filter) {
        try {
            String hql = "select prop " + fromPropopositionWithConcours + ", HistoGrade as histoGrade"
                    + ", HistoCadreEmploi as histoCadre " + whereHisto + " and prop.tableauAG is null ";
            Query query = prepareQueryListPropositions(tri, criteria, hql, null, true);

            if (query == null)
                return new ArrayList<PropositionAG>(0);
            if (firstLine > -1)
                query.setFirstResult((int) firstLine);
            if (limitLine > -1)
                query.setMaxResults((int) limitLine);

            return query.list();
        } catch (HibernateException hibEx) {
            throw new DaoException(hibEx);
        }
    }

    /**
     * @see IDaoPropositionAG#officialisePromotion(Integer, Boolean)
     */
    @Override
	@Transactional(propagation = Propagation.REQUIRED)
    public void officialisePromotion(Integer propositionId, Boolean promu) {
        try {
            Query query = getSession().createQuery(
                    "update PropositionAG as prop set decision =:EnumDecisionCAP, promu = :promu"
                            + " where prop.id =:propositionId");
            query.setInteger("propositionId", propositionId);
            query.setBoolean("promu", promu);
            query.setParameter("EnumDecisionCAP", promu ? EnumDecisionCAP.PROMU : EnumDecisionCAP.NON_PROMU);
            query.executeUpdate();
        } catch (HibernateException hibExc) {
            throw new DaoException(TypeDaoException.FATAL, hibExc);
        }
    }

    /**
     * @see fr.sedit.grh.coeur.ca.avg.dao.IDaoPropositionAG#findListPropositionByTableauGrade(fr.sedit.grh.coeur.ca.avg.model.TableauAG, fr.sedit.grh.coeur.cs.model.Grade)
     */
    @Override
	@SuppressWarnings("unchecked")
    @Transactional(propagation = Propagation.SUPPORTS, readOnly = true)
    public List<PropositionAG> findListPropositionByTableauGrade(TableauAG tableau, Grade grade) {
        try {
            Criteria crit = getSession().createCriteria(PropositionAG.class, "PROP").add(
                    Restrictions.eq("PROP.tableauAG", tableau));
            // objects preload
            crit.createAlias("PROP.gradeCible", "GRD", CriteriaSpecification.LEFT_JOIN).createAlias("PROP.carriere",
                    "CA", CriteriaSpecification.LEFT_JOIN).createAlias("CA.collectivite", "COLL",
                    CriteriaSpecification.LEFT_JOIN).createAlias("PROP.fichePosition", "FPA",
                    CriteriaSpecification.LEFT_JOIN).createAlias("FPA.positionAdmin", "PA",
                    CriteriaSpecification.LEFT_JOIN);

            if (grade != null)
                crit.add(Restrictions.eq("gradeCible", grade));

            crit.setResultTransformer(CriteriaSpecification.DISTINCT_ROOT_ENTITY);

            return crit.list();
        } catch (HibernateException hibExc) {
            throw new DaoException(TypeDaoException.FATAL, hibExc);
        }
    }

    /**
     * @see fr.sedit.grh.coeur.ca.avg.dao.IDaoPropositionAG#getPropositionInfoForDate(fr.sedit.grh.coeur.ca.avg.model.PropositionAG, java.lang.String, java.util.Date)
     */
    @Override
	@Transactional(propagation = Propagation.SUPPORTS, readOnly = true)
    public PropositionDetailsAGDTO getPropositionInfoForDate(PropositionAG proposition, String nameGradeAttribut, Date date, Date dateHisto, String carriereId) {
        try {
            String joinGrade = " join proposition.gradeCible as grade";
            if (nameGradeAttribut != null) {
                if (nameGradeAttribut.equals("gradeCible")) {
                    joinGrade = " join proposition.gradeCible as grade";
                } else if (nameGradeAttribut.equals("gradeAncien")) {
                    joinGrade = " join proposition.gradeAncien as grade";
                } else if (nameGradeAttribut.equals("gradeSPVActuel")) {
                    joinGrade = " join proposition.gradeSPVActuel as grade";
                } else if (nameGradeAttribut.equals("gradeSPVFutur")) {
                    joinGrade = " join proposition.gradeSPVFutur as grade";
                }
            }

            Query query = getSession().createQuery(
                    "select proposition.id as propositionId, ficheGradeEmploi as ficheGradeEmploi, histoGrade as histoGrade, histoCadreEmploi as histoCadreEmploi"
                            + " from PropositionAG as proposition"
                            //+ " join proposition.carriere as carriere"
                            + joinGrade
                            + " join grade.listHistoGrade as histoGrade with (histoGrade.dateDebut <= :dateHisto and (histoGrade.dateFin is null or histoGrade.dateFin >= :dateHisto))"
                            + " join histoGrade.cadreEmploi as cadreEmploi"
                            + " join cadreEmploi.listHistoCadreEmploi as histoCadreEmploi with (histoCadreEmploi.dateDebut <= :dateHisto and (histoCadreEmploi.dateFin is null or histoCadreEmploi.dateFin >= :dateHisto))"
                            + ", Carriere as carriere "
                            + " join carriere.listFicheGradeEmploi as ficheGradeEmploi with (ficheGradeEmploi.dateDebut <= :date and (ficheGradeEmploi.dateFin is null or ficheGradeEmploi.dateFin >= :date) and ficheGradeEmploi.gere = 'O' and ficheGradeEmploi.dateObsolete is null)"
                            + " where proposition = :proposition"
                            + " and carriere.id = :carriereId"
                            + " and ficheGradeEmploi.regroupement = proposition.codeRegroupement");

            query.setEntity("proposition", proposition);
            query.setDate("date", date);
            query.setDate("dateHisto", dateHisto);
            query.setString("carriereId", carriereId);
            query.setResultTransformer(Transformers.aliasToBean(PropositionDetailsAGDTO.class));
            return (PropositionDetailsAGDTO) query.uniqueResult();
        } catch (HibernateException hibEx) {
            throw new DaoException(TypeDaoException.FATAL, hibEx);
        }
    }
    
    /**
     * {@inheritDoc}
     * 
     * @see fr.sedit.grh.coeur.ca.avg.dao.IDaoPropositionAG#findCollectiviteByPropositionIds(java.util.List)
     */
	@Transactional(propagation = Propagation.SUPPORTS, readOnly = true)
	@Override
    @SuppressWarnings("unchecked")
	public List<CollectiviteNumerotationArreteParamDTO> findCollectiviteByPropositionIds(final List<Integer> propositionIds) {
    	
    	final Map<String, Object> queryParams = new HashMap<String, Object>();
    	queryParams.put("isEmployeur", true);
    	queryParams.put("propositionIds", propositionIds);
    	
    	final String queryString = "SELECT DISTINCT col_.id AS idCollectivite, col_.avecNumerotationArrete AS avecNumerotationArrete,"
    			+ " col_.typeNumerotationArrete AS typeNumerotationArrete FROM PropositionAG p_, Carriere car_, Collectivite col_"
    			+ " WHERE p_.carriere.id = car_.id AND car_.collectivite.id = col_.id"
    			+ " AND col_.employeur = :isEmployeur AND p_.id IN (:propositionIds)";
    	
    	try {
    		
    		final Query query = super.getSession().createQuery(queryString);
    		query.setProperties(queryParams);
    		query.setResultTransformer(Transformers4Enum.aliasToBean(CollectiviteNumerotationArreteParamDTO.class));

			return (List<CollectiviteNumerotationArreteParamDTO>)query.list();
		
		} catch (final HibernateException he) {
			throw new DaoException(super.getSession(), he);
		}
    }

    /**
     * @see fr.sedit.grh.coeur.ca.avg.dao.IDaoPropositionAG#loadPropositionWithContent(java.lang.Integer)
     */
    @Override
	@Transactional(propagation = Propagation.SUPPORTS, readOnly = true)
    public PropositionAG loadPropositionWithContent(Integer propositionId) {
        try {
            StringBuffer requete = new StringBuffer("from PropositionAG as prop");
            requete.append(" left join fetch prop.motifAvancement as motifAvancement");
            requete.append(" left join fetch prop.carriere as car");
            requete.append(" left join fetch car.libelleCarriere");
            requete.append(" left join fetch car.collectivite");
            requete.append(" left join fetch car.agent");
            requete.append(" left join fetch prop.tableauAG");

            requete.append(" where prop.id = :propositionId ");
            Query query = getSession().createQuery(requete.toString());
            query.setInteger("propositionId", propositionId);
            PropositionAG proposition = (PropositionAG) query.uniqueResult();

            return proposition;
        } catch (HibernateException hibExc) {
            throw new DaoException(TypeDaoException.FATAL, hibExc);
        }
    }

    /**
     * @see IDaoPropositionAG#findFichierImprimeByListPropositionsId(java.util.List)
     */
    @Override
    @SuppressWarnings("unchecked")
    @Transactional(propagation = Propagation.SUPPORTS, readOnly = true)
    public List<Imprime> findFichierImprimeByListPropositionsId(List<Integer> lstPropositionIds) {
    	try {

    		List<Imprime> result = new ArrayList<Imprime>(); 

    		if (lstPropositionIds == null || lstPropositionIds.size() == 0) {
    			return result; // Retourne une liste vide
    		}

    		// Récupération des imprimés (prop.imprime)
    		StringBuffer hql = new StringBuffer("select imp ");
    		hql.append("from PropositionAG as prop inner join prop.imprime as imp ")
    		.append("left join fetch imp.carriere car ")
    		.append("left join fetch car.agent agt ");
    		hql.append("where prop.id in (:propositionIds) ");
    		hql.append("and imp.fichier is not null ");
    		Query query = getSession().createQuery(hql.toString());
    		query.setParameterList("propositionIds", lstPropositionIds);
    		result.addAll(query.list());

    		// Récupération des imprimés (prop.imprimeSPV)
    		StringBuffer hqlSPV = new StringBuffer("select imp ");
    		hqlSPV.append("from PropositionAG as prop inner join prop.imprimeSPV as imp ")
    		.append("left join fetch imp.carriere car ")
    		.append("left join fetch car.agent agt ");
    		hqlSPV.append("where prop.id in (:propositionIds) ");
    		hqlSPV.append("and imp.fichier is not null ");
    		Query querySPV = getSession().createQuery(hqlSPV.toString());
    		querySPV.setParameterList("propositionIds", lstPropositionIds);
    		result.addAll(querySPV.list());

    		return result;

    	} catch (HibernateException hibExc) {
    		throw new DaoException(TypeDaoException.FATAL, hibExc);
    	}
    }
    
    @Override
	@Transactional(propagation = Propagation.SUPPORTS, readOnly = true)
    public Imprime findImprimeByPropositionId(Integer idProposition) {
        try {
            final StringBuffer hql = new StringBuffer(200);
            hql.append("SELECT imp ")
            	.append("FROM PropositionAG as prop ")
            	.append("left join prop.imprime as imp ")
            	.append("left join imp.carriere as car ")
            	.append("left join car.agent as agt ")
            	.append("WHERE prop.id = :idProposition ");
            final Query query = getSession().createQuery(hql.toString());
            query.setInteger("idProposition", idProposition);
            return (Imprime) query.uniqueResult();
        } catch (HibernateException hibExc) {
            throw new DaoException(TypeDaoException.FATAL, hibExc);
        }
    }

    /**
     * @see IDaoPropositionAG#findFichierImprimebyTableauId(java.lang.Integer)
     */
    @Override
	@SuppressWarnings("unchecked")
    @Transactional(propagation = Propagation.SUPPORTS, readOnly = true)
    public List<Imprime> findFichierImprimebyTableauId(Integer tableauId) {
        try {
            if (tableauId == null) {
                return new ArrayList<Imprime>(); // Retourne une liste vide
            }

            StringBuffer hql = new StringBuffer("select imp ");
            hql.append("from PropositionAG as prop inner join prop.imprime as imp ")
            	.append("inner join prop.tableauAG as tab ")
            	.append("left join fetch imp.carriere car ")
            	.append("left join fetch car.agent agt ");
            hql.append("where tab.id = :tableauId ");
            hql.append("and imp.fichier is not null ");
            Query query = getSession().createQuery(hql.toString());
            query.setInteger("tableauId", tableauId);
            return query.list();
        } catch (HibernateException hibExc) {
            throw new DaoException(TypeDaoException.FATAL, hibExc);
        }
    }

    static private final String hqlOfficialisee4Arretes = "select prop.id" + " from PropositionAG as prop"
            + " inner join prop.carriere as car" + " inner join car.agent as agent"
            + " inner join prop.motifAvancement as motAva" + " left join motAva.modeleType"
            + " left join prop.tableauAG as tableau" + " where " + " prop.promouvable = 1";

    /**
     * @see fr.sedit.grh.coeur.ca.avg.dao.IDaoPropositionAG#findListPropositionsOfficialisees4Arretes(fr.sedit.grh.coeur.ca.avg.model.TableauAG, java.util.List)
     */
    @Override
	@SuppressWarnings("unchecked")
    @Transactional(propagation = Propagation.SUPPORTS, readOnly = true)
    public List<Integer> findListPropositionsOfficialisees4Arretes(TableauAG tableau, List<Integer> propositionIds) {
        StringBuffer hqlBuffer = new StringBuffer(hqlOfficialisee4Arretes);
        if (tableau != null)
            hqlBuffer.append(" and ").append("tableau = :tableau");
        if (propositionIds != null)
            hqlBuffer.append(" and ").append("prop.id in (:propositionIds)");

        Query query = getSession().createQuery(hqlBuffer.toString());

        if (tableau != null)
            query.setEntity("tableau", tableau);
        if (propositionIds != null)
            query.setParameterList("propositionIds", propositionIds);

        return query.list();
    }

    static private final String fromWhereInjectionHql = " from PropositionAG as prop"
            + " inner join prop.carriere as car" + " inner join car.agent as agent"
            + " inner join car.libelleCarriere as libCar"
            + " inner join prop.statutActuel as statutActuel" + " inner join prop.statutAvancement as statutAvancement"
            + " inner join prop.gradeCible as gradeCible" + " inner join prop.gradeAncien as gradeAncien"
            + " inner join prop.chevronAncien as chevAnc" + " inner join prop.chevronCible as chevCible"
            + " left join prop.tableauAG as tableau" + " left join prop.imprime as impr"
            + " where prop.promouvable = true" + " and (tableau is null or tableau.etatTableau > "
            + EnumPhaseTableauAG.ELABORATION_TABLEAU.getCode() + ")" + " and prop.modeCreation != '"
            + EnumModeCreation.SIMULATION.getCode() + "'"
            + " and (TO_CHAR(prop.dateRetenue, 'YYYY.MM') <= car.collectivite.periodePaie"
            + " OR libCar.type = 'V' )";

    static private final String injectionIdsHql = "select distinct prop.id" + fromWhereInjectionHql;

    /**
     * @see fr.sedit.grh.coeur.ca.avg.dao.IDaoPropositionAG#findInjectionPropIdsWithCriteria(java.util.Map)
     */
    @Override
	@Transactional(propagation = Propagation.SUPPORTS, readOnly = true)
    @SuppressWarnings("unchecked")
    public List<Integer> findInjectionPropIdsWithCriteria(Map<String, Object> propositionCriteria) {
        try {
            Query query = prepareQueryListPropositions(null, propositionCriteria, injectionIdsHql, null, false);
            return query.list();
        } catch (HibernateException hibExc) {
            throw new DaoException(TypeDaoException.FATAL, hibExc);
        }
    }

    /**
     * @see fr.sedit.grh.coeur.ca.avg.dao.IDaoPropositionAG#findListPropositionAG(java.util.Map, java.util.Map)
     */
    @Override
	@SuppressWarnings("unchecked")
    @Transactional(propagation = Propagation.SUPPORTS, readOnly=true)
    public List<PropositionAG> findListPropositionAG(Map<String, Integer> tri, Map<String, Object> criteres) {
        try {
            Query query = prepareQueryListPropositions(tri, criteres, "select prop " + fromPropoposition, null, true);
            if (query == null)
                return new ArrayList<PropositionAG>(0);

            return query.list();
        } catch (HibernateException hibExc) {
            throw new DaoException(TypeDaoException.FATAL, hibExc);
        }
    }

    /**
     * @see fr.sedit.grh.coeur.ca.avg.dao.IDaoPropositionAG#findListPorpositionAGByConcoursAgent(java.lang.String)
     */
    @Override
	@SuppressWarnings("unchecked")
    @Transactional(propagation = Propagation.SUPPORTS, readOnly=true)
    public List<PropositionAG> findListPorpositionAGByConcoursAgent(String idConcoursAgent) {
        try {
            StringBuffer hqlQuery = new StringBuffer(106);
    
            hqlQuery.append(" FROM PropositionAG prop WHERE prop.reussiteConcours.id = :idConcoursAgent");
    
            Query query = getSession().createQuery(hqlQuery.toString());
            query.setString("idConcoursAgent", idConcoursAgent);
            if (log.isDebugEnabled()) {
                log.debug(query.toString());
            }
            return query.list();
        } catch (HibernateException hibExc) {
            throw new DaoException(TypeDaoException.FATAL, hibExc);
        }
    }

    @Override
	@SuppressWarnings("unchecked")
    @Transactional(propagation = Propagation.SUPPORTS, readOnly = true)
    public List<PropositionAG> loadListPropositionByListIdForContexteArrete(List<Integer> lstid) {
        try {
            if (lstid != null && lstid.size() > 0) {
                StringBuilder hqlQuery = new StringBuilder();
                hqlQuery.append(" SELECT distinct (p) from PropositionAG p ");
                hqlQuery.append(" left join fetch p.carriere  carriere");
                hqlQuery.append(" left join fetch carriere.agent agt ");
                hqlQuery.append(" left join fetch p.motifAvancement motifAv");
                hqlQuery.append(" left join fetch motifAv.modeleType");
                hqlQuery.append(" WHERE p.id in (:listID)");
                Query query = getSession().createQuery(hqlQuery.toString());
                query.setParameterList("listID", lstid);
                return query.list();
            } else {
                return null;
            }
        } catch (HibernateException hibExc) {
            throw new DaoException(TypeDaoException.FATAL, hibExc);
        }
    }
    
    /**
     * @see fr.sedit.grh.coeur.ca.avg.dao.IDaoPropositionAG#findPropositionAGByIdImprime(java.lang.String)
     */
    @Override
	@SuppressWarnings("unchecked")
    @Transactional(propagation = Propagation.SUPPORTS, readOnly = true)
    public List<PropositionAG> findPropositionAGByIdImprime(String idImprime) {
    	try {
            StringBuilder hqlQuery = new StringBuilder(500);
           
            hqlQuery.append(" from PropositionAG prop " +
                            " LEFT JOIN FETCH prop.tableauAG tab " +
                            " where (prop.imprime.id=:idImprime or prop.imprimeSPV.id=:idImprime)" );

            Query query = this.getSession().createQuery(hqlQuery.toString());

            query.setParameter("idImprime", idImprime );
            return query.list();
        } catch (HibernateException e) {
            throw new DaoException(e);
        }
    }

    /**
     * @see fr.sedit.grh.coeur.ca.avg.dao.IDaoPropositionAG#findInfosAgentByProposition(java.lang.Integer)
     */
    @Override
    @Transactional(propagation = Propagation.SUPPORTS, readOnly = true)
    public AgentDTO findInfosAgentByProposition(Integer idProposition) {
    	try {
    		StringBuilder hqlQuery = new StringBuilder(500);

    		hqlQuery.append("select ag.nom as nom, ag.prenom as prenom, ag.matricule as matricule " +
    				" from PropositionAG prop " +
    				" LEFT JOIN prop.carriere.agent ag " +
    				" where prop.id=:idProposition" );

    		Query query = this.getSession().createQuery(hqlQuery.toString());

    		query.setParameter("idProposition", idProposition );
    		query.setResultTransformer(Transformers.aliasToBean(AgentDTO.class));
            
    		return (AgentDTO)query.uniqueResult();
    	} catch (HibernateException e) {
    		throw new DaoException(e);
    	}
    }
    
    /**
     * @see fr.sedit.grh.coeur.ca.avg.dao.IDaoPropositionAG#countPropositionsPromouvableByTableauAG(java.lang.String)
     */
    @Override
    @Transactional(propagation = Propagation.SUPPORTS, readOnly = true)
    public Long countPropositionsPromouvableByTableauAG(String code) {
        try {
            Criteria crit = getSession().createCriteria(PropositionAG.class);
            crit.setProjection(Projections.rowCount());
            
            crit.createAlias("tableauAG", "TAG");
            
            crit.add(Restrictions.eq("TAG.code", code));
            crit.add(Restrictions.eq("promouvable", true));

            return  Long.valueOf(crit.uniqueResult().toString());
        } catch (HibernateException hibExc) {
            throw new DaoException(hibExc);
        }
    }    
    
    @Override
    @Transactional(propagation = Propagation.REQUIRED)
    public PropositionAG save(PropositionAG entity) {
    	this.getSession().saveOrUpdate(entity);
    	return entity;
    }
    
    @Override
    @Transactional(propagation = Propagation.SUPPORTS, readOnly = true)
    public List<Integer> orderPropositionAGIdByCollectivite(List<Integer> propositionIds) {
        try {
            StringBuilder hqlQuery = new StringBuilder();
            hqlQuery.append("select prop.id ")
                    .append(" from PropositionAG prop ")
                    .append(" LEFT JOIN prop.carriere ca ")
                    .append(" WHERE prop.id in :propositionIds") 
                    .append(" ORDER BY ca.collectivite");
            Query query = this.getSession().createQuery(hqlQuery.toString());            
            query.setParameterList("propositionIds", propositionIds);
            return query.list();
        } catch (HibernateException e) {
            throw new DaoException(e);
        }
    }
    
    /**
     * MANTIS 35489 SROM 01/2017 : on borne la date de réference aux dates du tableau s'il existe
     * @param idTableauAG
     * @return
     */
    @Override
    public Date getDateRefFromTableauAG(Integer idTableauAG) {
        Date dateRef = new Date();
        if (idTableauAG != null) {
        	Query qryRecupTableau = getSession().createQuery("from TableauAG tabAG where tabAG.id = :idTabAG ");
        	qryRecupTableau.setInteger("idTabAG", idTableauAG);
        	TableauAG tabAG = (TableauAG)qryRecupTableau.uniqueResult();
        	if (UtilsDate.compareByDateOnly(dateRef, tabAG.getDateDebut()) < 0) {
        		dateRef = (Date)tabAG.getDateDebut().clone();
        	} else if (UtilsDate.compareByDateOnly(dateRef, tabAG.getDateFin()) > 0) { 
        		dateRef = (Date)tabAG.getDateFin().clone();
        	}
        }    	
        return dateRef;
    }
    



    @Override
  	@SuppressWarnings("unchecked")
      @Transactional(propagation = Propagation.SUPPORTS, readOnly = true)
      public List<Integer> findListPropositionIdByMatricule(String matricule, String typeavancement, String typeProposition, 
    		  int tableauId, String codeCadre, String codeCadreCible) {
    	List<Integer> ids = new ArrayList<>();
 
    	try {
              StringBuilder hql = new StringBuilder(300);
              hql.append("SELECT prop.identifiant                                                                             "
            		  +"FROM CAAVG_PropositionAG prop, RH_CARRIERE car, rh_agent ag, rh_grade grd_debut, rh_grade grd_fin   "
            		  +"WHERE  prop.carriererir = car.ROO_IMA_REF                                                           "
            		  +"AND   car.agent = ag.roo_ima_ref                                                                    "
            		  +"AND   prop.gradeancienrir = grd_debut.roo_ima_ref                                                   "
            		  +"AND   prop.gradeciblerir = grd_fin.roo_ima_ref                                                      "
            		  +"AND   ag.matricule=:matricule                                                                       "
            		  +"and   typeproposition = :typeProposition                                                            "
            		  +"and  tableauAgId = :tableauId 	                                                                  "
            		  +"and  prop.typeavancement = :typeavancement                                                          "
            		  +"AND  grd_debut.codgrade = :codeGrade                                                                "
            		  +"AND   grd_fin.codgrade = :codeGradeCible                                                            ");
              
              Query query = getSession().createSQLQuery(hql.toString());
              query.setString("matricule", matricule);
              query.setString("typeavancement", typeavancement);
              query.setString("typeProposition", typeProposition);
              query.setInteger("tableauId", tableauId);
              query.setString("codeGrade", codeCadre);
              query.setString("codeGradeCible", codeCadreCible);
              List<BigDecimal> rangsAG = (List<BigDecimal>) query.list();
  			for(BigDecimal rangAg : rangsAG) {  				
  				if(rangAg!=null) {				
  					ids.add(rangAg.intValue());
  				}
  			}
        } catch (HibernateException hibExc) {
            throw new DaoException(getSession(), hibExc);
        }
  	return ids;
          
      }
      
    @Override
  	@SuppressWarnings("unchecked")
      @Transactional(propagation = Propagation.SUPPORTS, readOnly = true)
      public List<Integer> findListPropositionIdByMatricules(List<String> matricules, String typeavancement) {
          
    	List<Integer> ids = new ArrayList<>();
    	try {

              StringBuilder hql = new StringBuilder(300);
              hql.append(" select prop.id from PropositionAG prop "
  					+  "  LEFT JOIN prop.carriere.agent ag      "
  					+  " Where ag.matricule in :matricules    	"
  					+  " and typeavancement = :typeavancement 				"
  					+  " and typeproposition = 'PRO'            ");
              Query query = getSession().createQuery(hql.toString());
              query.setParameterList("matricules", matricules);
              query.setString("typeavancement", typeavancement);
           
              List<BigDecimal> rangsAG = (List<BigDecimal>) query.list();
    			for(BigDecimal rangAg : rangsAG) {  				
    				if(rangAg!=null) {				
    					ids.add(rangAg.intValue());
    				}
    			}
          } catch (HibernateException hibExc) {
              throw new DaoException(getSession(), hibExc);
          }
    	return ids;
      }
      
	@Override
	@Transactional(propagation = Propagation.REQUIRED)
	public void updatePromuClassementDateNominationDateEffetFromIdProposition(Integer idProposition, Date datePromotion,
			Integer idClassement, boolean promu) {
		try {
			final StringBuffer hql = new StringBuffer();
			final Query query;
			if (idClassement != null) {
				hql.append("UPDATE FROM PropositionAG prop " 
						+ "set prop.promu = :promu , "
						+ "prop.rangClassementAG = :idClassement, " 
						+ "prop.dateNomination = :datePromotion, "
						+ "prop.dateEffet = :datePromotion ");
				hql.append(" WHERE prop.id = :idProposition ");
				query = getSession().createQuery(hql.toString());
				query.setInteger("idClassement", idClassement);
				query.setInteger("idProposition", idProposition);
				query.setBoolean("promu", promu);
				query.setDate("datePromotion", datePromotion);
				query.executeUpdate();
			} else {
				hql.append("UPDATE FROM PropositionAG prop " 
						+ "set prop.promu = :promu , "
						+ "prop.rangClassementAG = null , "
						+ "prop.dateNomination = :datePromotion, " 
						+ "prop.dateEffet = :datePromotion ");
				hql.append(" WHERE prop.id = :idProposition ");
				query = getSession().createQuery(hql.toString());
				query.setInteger("idProposition", idProposition);
				query.setBoolean("promu", promu);
				query.setDate("datePromotion", datePromotion);
				query.executeUpdate();
			}

		} catch (HibernateException hibEx) {
			throw new DaoException(hibEx);
		}
	}

  	@Override
  	public Date getDatePromotionFromTableauAG(Integer idTableauAG) {
  		Date datePromotion = new Date();
  		if (idTableauAG != null) {
  			Query qryRecupTableau = getSession().createQuery("from TableauAG tabAG where tabAG.id = :idTabAG ");
  			qryRecupTableau.setInteger("idTabAG", idTableauAG);
  			TableauAG tabAG = (TableauAG) qryRecupTableau.uniqueResult();
  			datePromotion = tabAG.getDatePromotion();
  		}
  		return datePromotion;
  	}
  	
  	@Override
  	public List<Integer> getIdClassementFromValeurSaisieLibre(String saisieLibre) {
  		List<Integer> ids = new ArrayList<>();
  		if (saisieLibre != null) {
  			Query qryRecupTableau = getSession().createQuery("from RangClassementAG rangAG where rangAG.saisieLibre = :saisieLibre ");
  			qryRecupTableau.setString("saisieLibre", saisieLibre);
			for(Object o :  qryRecupTableau.list() ) {
				RangClassementAG rangAg = (RangClassementAG) o;
  				if(rangAg!=null) {				
  					ids.add(rangAg.getId());
  				}
			}

  		}
  		return ids;
  		
  	}
  	
  	@Override
  	public List<Integer> getIdClassementFromValeur(String valeur) {
		List<Integer> ids = new ArrayList<>();
  		if (valeur != null) {
  			StringBuilder insert = new StringBuilder();
  			
  			insert.append(  					"select CAAVG_RangClassementAG.identifiant "
  					+ "from CAAVG_RangClassementAG , CAPAR_valeurclassementag " 
  					+ "where CAAVG_RangClassementAG.valeur = CAPAR_valeurclassementag.valeur "
  					+ "and CAPAR_valeurclassementag.libelle = :valeur");
  			Query query = getSession().createSQLQuery(insert.toString());
  			query.setString("valeur", valeur);
  			List<BigDecimal> rangsAG = (List<BigDecimal>) query.list();
  			for(BigDecimal rangAg : rangsAG) {  				
  				if(rangAg!=null) {				
  					ids.add(rangAg.intValue());
  				}
  			}
  		}
  		return ids;
  		
  	}
  	

  	@Override
	@Transactional(propagation = Propagation.REQUIRED)
	public Integer insertNouveauRangClassementAg(String saisieLibre) {
		Integer nouvelIdentifiant = null;

		try {
			StringBuilder insert = new StringBuilder();
			insert.append("INSERT INTO caavg_RangClassementAG (identifiant, version, saisieLibre) VALUES "
					+ "((SELECT MAX(identifiant) FROM caavg_RangClassementAG) + 1, '0', :saisieLibre)");
			Query query = getSession().createSQLQuery(insert.toString());
			query.setString("saisieLibre", saisieLibre);
			query.executeUpdate();

			Query selectQuery = getSession().createSQLQuery("SELECT MAX(identifiant) FROM caavg_RangClassementAG");
			nouvelIdentifiant = Integer.valueOf(selectQuery.uniqueResult().toString());

		} catch (HibernateException hibExc) {
			throw new DaoException(hibExc);
		}
		return nouvelIdentifiant;
	}
  	
    @Override
    @Transactional(propagation = Propagation.SUPPORTS, readOnly = true)
    public boolean isValeurExisting(String valeur) {
        try {
            final StringBuilder hql = new StringBuilder("FROM RangClassementAG rang WHERE rang.valeur = :valeur");
            final Query query = getSession().createQuery(hql.toString());
            query.setString("valeur", valeur);
            final List<RangClassementAG> listeRang = query.list();
            return (listeRang != null && !listeRang.isEmpty());
        } catch (HibernateException hibExc) {
            throw new DaoException( hibExc);
        }
    }
    
  	
    @Override
    @Transactional(propagation = Propagation.SUPPORTS, readOnly = true)
    public boolean isSaisieLibreExisting(String saisieLibre) {
        try {
            final StringBuilder hql = new StringBuilder("FROM RangClassementAG rang WHERE rang.saisieLibre = :saisieLibre");
            final Query query = getSession().createQuery(hql.toString());
            query.setString("saisieLibre", saisieLibre);
            final List<RangClassementAG> listeRang = query.list();
            return (listeRang != null && !listeRang.isEmpty());
        } catch (HibernateException hibExc) {
            throw new DaoException( hibExc);
        }
    }
  	
  	
  		
}
 
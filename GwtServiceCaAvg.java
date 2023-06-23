package fr.bl.server.grh.ca.avg;

import com.google.gwt.user.server.rpc.RemoteServiceServlet;
import fr.bl.client.core.exception.SeditGwtException;
import fr.bl.client.core.gab.Workspace;
import fr.bl.client.core.refui.base.event.BLEventPopup;
import fr.bl.client.grh.ca.avg.model.GrilleAncienneteGWT;
import fr.bl.client.grh.ca.avg.model.RatiosQuotasParTableauGWT;
import fr.bl.client.grh.ca.avg.model.dto.*;
import fr.bl.client.grh.ca.avg.remote.IGwtServiceCaAvg;
import fr.bl.client.grh.ca.par.model.ParamRangClassementAGGWT;
import fr.bl.client.grh.ca.par.model.ValeurClassementAGGWT;
import fr.bl.client.grh.ca.par.model.dto.*;
import fr.bl.client.grh.coeur.ca.avg.model.AvisAGGWT;
import fr.bl.client.grh.coeur.ca.avg.model.PropositionAGGWT;
import fr.bl.client.grh.coeur.ca.avg.model.RangClassementAGGWT;
import fr.bl.client.grh.coeur.ca.avg.model.TableauAGGWT;
import fr.bl.client.grh.coeur.ca.avg.model.enums.*;
import fr.bl.client.grh.coeur.ca.par.model.EcheanceCAPGWT;
import fr.bl.client.grh.coeur.ca.par.model.ParamCaOrgaGWT;
import fr.bl.client.grh.coeur.ca.par.model.enums.EnumDecisionCAPGWT;
import fr.bl.client.grh.coeur.ca.par.model.enums.EnumTypeAEAGGWT;
import fr.bl.client.grh.coeur.ca.par.model.enums.EnumTypePresentationGWT;
import fr.bl.client.grh.coeur.ca.par.model.enums.EnumTypeTableauGWT;
import fr.bl.client.grh.coeur.cs.model.AgentGWT;
import fr.bl.client.grh.coeur.cs.model.GradeGWT;
import fr.bl.client.grh.coeur.cs.model.dto.*;
import fr.bl.client.grh.coeur.cs.model.parametrage.CadreStatutaireGWT;
import fr.bl.client.grh.coeur.cs.model.parametrage.CollectiviteGWT;
import fr.bl.client.grh.coeur.cs.model.parametrage.OrganismeGWT;
import fr.bl.client.grh.coeur.nr.model.dto.RoleOrganisationnelDTOGWT;
import fr.bl.client.grh.coeur.sm.model.UtilisateurSMRHGWT;
import fr.bl.client.grh.common.security.model.UserDTOGRHGWT;
import fr.bl.server.clone.IDtoConvertor;
import fr.bl.server.grh.exception.SeditGwtTry;
import fr.bl.shared.core.dto.models.commons.IKey;
import fr.bl.shared.core.dto.models.commons.Key;
import fr.bl.shared.grh.car.dto.CalculatriceCarriereDTO;
import fr.bl.shared.grh.car.dto.InjectionFicheCarriereRetourDTO;
import fr.bl.shared.grh.car.enums.EnumCalculatriceOperation;
import fr.bl.shared.grh.coeur.dto.CollectiviteNumerotationArreteParamDTO;
import fr.bl.shared.grh.coeur.dto.NumeroArreteDTO;
import fr.sedit.core.batchs.BatchModeEnum;
import fr.sedit.core.batchs.IBatchManager;
import fr.sedit.core.batchs.jobs.IJobCommandInfo;
import fr.sedit.core.exception.BusinessException;
import fr.sedit.core.exception.FusionException;
import fr.sedit.core.exception.SeditException;
import fr.sedit.core.exception.TechnicalException;
import fr.sedit.core.tools.UtilsDate;
import fr.sedit.grh.ca.avg.model.GrilleAnciennete;
import fr.sedit.grh.ca.avg.model.RatiosQuotasParTableau;
import fr.sedit.grh.ca.avg.model.dto.*;
import fr.sedit.grh.ca.avg.usecases.*;
import fr.sedit.grh.ca.par.model.ParamRangClassementAG;
import fr.sedit.grh.ca.par.model.ValeurClassementAG;
import fr.sedit.grh.ca.par.model.dto.*;
import fr.sedit.grh.coeur.ca.avg.model.AvisAG;
import fr.sedit.grh.coeur.ca.avg.model.PropositionAG;
import fr.sedit.grh.coeur.ca.avg.model.RangClassementAG;
import fr.sedit.grh.coeur.ca.avg.model.TableauAG;
import fr.sedit.grh.coeur.ca.avg.model.enums.*;
import fr.sedit.grh.coeur.ca.avg.usecases.IUcAvisAG;
import fr.sedit.grh.coeur.ca.avg.usecases.IUcPropositionAG;
import fr.sedit.grh.coeur.ca.avg.usecases.IUcTableauAG;
import fr.sedit.grh.coeur.ca.par.model.EcheanceCAP;
import fr.sedit.grh.coeur.ca.par.model.ParamCaOrga;
import fr.sedit.grh.coeur.ca.par.model.enums.EnumDecisionCAP;
import fr.sedit.grh.coeur.ca.par.model.enums.EnumTypeAEAG;
import fr.sedit.grh.coeur.ca.par.model.enums.EnumTypePresentation;
import fr.sedit.grh.coeur.ca.par.model.enums.EnumTypeTableau;
import fr.sedit.grh.coeur.ca.usecases.cr.IUcCrudCadreEmploi;
import fr.sedit.grh.coeur.cs.model.Grade;
import fr.sedit.grh.coeur.cs.model.dto.*;
import fr.sedit.grh.coeur.cs.model.parametrage.CadreStatutaire;
import fr.sedit.grh.coeur.cs.model.parametrage.Collectivite;
import fr.sedit.grh.coeur.cs.model.parametrage.Organisme;
import fr.sedit.grh.coeur.cs.usecases.IUcAgent4Grade;
import fr.sedit.grh.coeur.cs.usecases.IUcGrade;
import fr.sedit.grh.coeur.cs.usecases.IUcService4Grade;
import fr.sedit.grh.coeur.gc.usecases.IUcPosteBudgetaireARecalculer;
import fr.sedit.grh.coeur.gc.usecases.IUcPosteTravailOccupation;
import fr.sedit.grh.coeur.sm.model.ContextSMRH;
import fr.sedit.grh.coeur.sm.model.UtilisateurSMRH;
import org.apache.commons.logging.LogFactory;
import org.springframework.context.ApplicationContext;
import org.springframework.web.context.ContextLoader;

import java.io.Serializable;
import java.util.*;

@SuppressWarnings("unchecked")
public class GwtServiceCaAvg extends RemoteServiceServlet implements IGwtServiceCaAvg {

    private static final long serialVersionUID = 165455288298190119L;
    private static final org.apache.commons.logging.Log log = LogFactory.getLog(GwtServiceCaAvg.class);

    // ---------- Debut Injection Spring ----------
    private IUcTableauAG ucTableauAG = null;
    private IUcReussiteConcours ucReussiteConcours = null;
    private IUcDescriptionCAPAG ucDescriptionCAPAG;
    private IUcVerificationParametrageCAP ucVerificationParametrageCAP;
    private IUcVerificationPropositionAGCAP ucVerificationPropositionAGCAP;
    private IUcElaborationCAP ucElaborationCAP;
    private IUcFinalisationCAP ucFinalisationCAP;
    private IUcAgent4Grade ucAgent4Grade = null;
    private IUcPropositionAG ucPropositionAG = null;
    private IUcAvisAG ucAvisAG = null;
    private IUcGrade ucGrade = null;
    private IUcGrilleAnciennete ucGrilleAnciennete = null;
    private IUcInjectionAG ucInjectionAG = null;
    private IUcService4Grade ucService4Grade = null;
    private IUcRatiosQuotasParTableau ucRatiosQuotasParTableau;
    private IUcImprimeAG ucImprimeAG;
    private IUcSuiviAvisAG ucSuiviAvisAG;
    private IUcSaisieAvisAG ucSaisieAvisAG;
    private IDtoConvertor mapper = null;
    private IUcPosteBudgetaireARecalculer ucPosteBudgetaireARecalculer;
    private IUcPosteTravailOccupation ucPosteTravailOccupation;
    private IUcCrudCadreEmploi ucCrudCadreEmploi;
    private IUcCalculatrice ucCalculatrice;

    public void setUcTableauAG(IUcTableauAG ucTableauAG) {
        this.ucTableauAG = ucTableauAG;
    }

    public void setUcReussiteConcours(IUcReussiteConcours ucReussiteConcours) {
        this.ucReussiteConcours = ucReussiteConcours;
    }

    public void setUcDescriptionCAPAG(IUcDescriptionCAPAG ucDescriptionCAPAG) {
        this.ucDescriptionCAPAG = ucDescriptionCAPAG;
    }

    public void setUcVerificationParametrageCAP(IUcVerificationParametrageCAP ucVerificationParametrageCAP) {
        this.ucVerificationParametrageCAP = ucVerificationParametrageCAP;
    }

    public void setUcVerificationPropositionAGCAP(IUcVerificationPropositionAGCAP ucVerificationPropositionAGCAP) {
        this.ucVerificationPropositionAGCAP = ucVerificationPropositionAGCAP;
    }

    public void setUcElaborationCAP(IUcElaborationCAP ucElaborationCAP) {
        this.ucElaborationCAP = ucElaborationCAP;
    }

    public void setUcFinalisationCAP(IUcFinalisationCAP ucFinalisationCAP) {
        this.ucFinalisationCAP = ucFinalisationCAP;
    }

    public void setUcAgent4Grade(IUcAgent4Grade ucAgent4Grade) {
        this.ucAgent4Grade = ucAgent4Grade;
    }

    public void setUcPropositionAG(IUcPropositionAG ucPropositionAG) {
        this.ucPropositionAG = ucPropositionAG;
    }

    public void setUcAvisAG(IUcAvisAG ucAvisAG) {
        this.ucAvisAG = ucAvisAG;
    }

    public void setUcGrade(IUcGrade ucGrade) {
        this.ucGrade = ucGrade;
    }

    public void setUcGrilleAnciennete(IUcGrilleAnciennete ucGrilleAnciennete) {
        this.ucGrilleAnciennete = ucGrilleAnciennete;
    }

    public void setUcInjectionAG(IUcInjectionAG ucInjectionAG) {
        this.ucInjectionAG = ucInjectionAG;
    }

    public void setUcService4Grade(IUcService4Grade ucService4Grade) {
        this.ucService4Grade = ucService4Grade;
    }

    public void setUcRatiosQuotasParTableau(IUcRatiosQuotasParTableau ucRatiosQuotasParTableau) {
        this.ucRatiosQuotasParTableau = ucRatiosQuotasParTableau;
    }

    public void setUcImprimeAG(IUcImprimeAG ucImprimeAG) {
        this.ucImprimeAG = ucImprimeAG;
    }

    public void setUcSuiviAvisAG(IUcSuiviAvisAG ucSuiviAvisAG) {
        this.ucSuiviAvisAG = ucSuiviAvisAG;
    }

    public void setUcSaisieAvisAG(IUcSaisieAvisAG ucSaisieAvisAG) {
        this.ucSaisieAvisAG = ucSaisieAvisAG;
    }

    public void setMapper(IDtoConvertor mapper) {
        this.mapper = mapper;
    }

    public void setUcPosteBudgetaireARecalculer(IUcPosteBudgetaireARecalculer ucPosteBudgetaireARecalculer) {
        this.ucPosteBudgetaireARecalculer = ucPosteBudgetaireARecalculer;
    }

    public void setUcPosteTravailOccupation(IUcPosteTravailOccupation ucPosteTravailOccupation) {
        this.ucPosteTravailOccupation = ucPosteTravailOccupation;
    }

    public void setUcCrudCadreEmploi(IUcCrudCadreEmploi ucCrudCadreEmploi) {
        this.ucCrudCadreEmploi = ucCrudCadreEmploi;
    }

    public void setUcCalculatrice(IUcCalculatrice ucCalculatrice) {
        this.ucCalculatrice = ucCalculatrice;
    }
    // ---------- Fin Injection Spring ----------


    /**
     * @see fr.bl.client.grh.ca.avg.remote.IGwtServiceCaAvg#searchEnCoursTableauAG(java.lang.String, java.lang.String)
     */
    @Override
    public List<EnCoursTableauAGDTOGWT> searchEnCoursTableauAG(final String idOrganisme, final String type) throws SeditGwtException {
        try {
            List<EnCoursTableauAGDTO> list = ucTableauAG.searchEnCoursTableauAG(idOrganisme, type);
            if (list == null) return new ArrayList<EnCoursTableauAGDTOGWT>();
            return mapper.cloneToGwt(list, EnCoursTableauAGDTOGWT.class);
        } catch (BusinessException e) {
            if (SeditException.Type.MULTIPLE_EXCEPTION.equals(e.getType()))
                throw new SeditGwtException(e.getExceptionMap());
            else throw new SeditGwtException(SeditGwtException.TYPE_BUSINESS, e);
        } catch (TechnicalException e) {
            if (SeditException.Type.MULTIPLE_EXCEPTION.equals(e.getType()))
                throw new SeditGwtException(e.getExceptionMap());
            else throw new SeditGwtException(SeditGwtException.TYPE_TECHNICAL, e);
        } catch (Exception e) {
            throw new SeditGwtException(SeditGwtException.TYPE_INCONNUE, e);
        }
    }

    /**
     * @see fr.bl.client.grh.ca.avg.remote.IGwtServiceCaAvg#loadReussiteConcoursDTONonValidees(fr.bl.client.grh.coeur.cs.model.parametrage.OrganismeGWT)
     */
    @Override
    public List<ReussiteConcoursDTOGWT> loadReussiteConcoursDTONonValidees(OrganismeGWT organisme) throws SeditGwtException {
        try {
            List<ReussiteConcoursDTO> list = ucReussiteConcours.loadReussiteConcoursDTONonValidees(mapper.cloneToModel(organisme, Organisme.class));
            if (list == null) return new ArrayList<ReussiteConcoursDTOGWT>();
            return mapper.cloneToGwt(list, ReussiteConcoursDTOGWT.class);
        } catch (BusinessException e) {
            if (SeditException.Type.MULTIPLE_EXCEPTION.equals(e.getType()))
                throw new SeditGwtException(e.getExceptionMap());
            else throw new SeditGwtException(SeditGwtException.TYPE_BUSINESS, e);
        } catch (TechnicalException e) {
            if (SeditException.Type.MULTIPLE_EXCEPTION.equals(e.getType()))
                throw new SeditGwtException(e.getExceptionMap());
            else throw new SeditGwtException(SeditGwtException.TYPE_TECHNICAL, e);
        } catch (Exception e) {
            throw new SeditGwtException(SeditGwtException.TYPE_INCONNUE, e);
        }
    }

    /**
     * @see fr.bl.client.grh.ca.avg.remote.IGwtServiceCaAvg#loadPromotions(fr.bl.client.grh.coeur.cs.model.parametrage.OrganismeGWT)
     */
    @Override
    public List<ReussiteConcoursDTOGWT> loadPromotions(OrganismeGWT organisme) throws SeditGwtException {
        try {
            List<ReussiteConcoursDTO> list = ucReussiteConcours.loadPromotions(mapper.cloneToModel(organisme, Organisme.class));
            if (list == null) return new ArrayList<ReussiteConcoursDTOGWT>();
            return mapper.cloneToGwt(list, ReussiteConcoursDTOGWT.class);
        } catch (BusinessException e) {
            if (SeditException.Type.MULTIPLE_EXCEPTION.equals(e.getType()))
                throw new SeditGwtException(e.getExceptionMap());
            else throw new SeditGwtException(SeditGwtException.TYPE_BUSINESS, e);
        } catch (TechnicalException e) {
            if (SeditException.Type.MULTIPLE_EXCEPTION.equals(e.getType()))
                throw new SeditGwtException(e.getExceptionMap());
            else throw new SeditGwtException(SeditGwtException.TYPE_TECHNICAL, e);
        } catch (Exception e) {
            throw new SeditGwtException(SeditGwtException.TYPE_INCONNUE, e);
        }
    }

    /**
     * @see fr.bl.client.grh.ca.avg.remote.IGwtServiceCaAvg#createPropositionConcours(ReussiteConcoursDTOGWT)
     */
    @Override
    public PropositionAGGWT createPropositionConcours(ReussiteConcoursDTOGWT reussiteConcoursDTOGWT) throws SeditGwtException {
        try {
            ReussiteConcoursDTO reussiteConcours = mapper.cloneToModel(reussiteConcoursDTOGWT, ReussiteConcoursDTO.class);
            PropositionAG prop = ucReussiteConcours.createPropositionConcours(reussiteConcours);
            prop = ucPropositionAG.calculClassementProposition(prop.getId());
            return mapper.cloneToGwt(prop, PropositionAGGWT.class);
        } catch (BusinessException e) {
            e.printStackTrace();
            if (SeditException.Type.MULTIPLE_EXCEPTION.equals(e.getType()))
                throw new SeditGwtException(e.getExceptionMap());
            else throw new SeditGwtException(SeditGwtException.TYPE_BUSINESS, e);
        } catch (TechnicalException e) {
            e.printStackTrace();
            if (SeditException.Type.MULTIPLE_EXCEPTION.equals(e.getType()))
                throw new SeditGwtException(e.getExceptionMap());
            else throw new SeditGwtException(SeditGwtException.TYPE_TECHNICAL, e);
        } catch (Exception e) {
            e.printStackTrace();
            throw new SeditGwtException(SeditGwtException.TYPE_INCONNUE, e);
        }
    }

    /**
     * @see fr.bl.client.grh.ca.avg.remote.IGwtServiceCaAvg#recalculClassementPropositionAG(java.lang.Integer)
     */
    @Override
    public PropositionAGGWT recalculClassementPropositionAG(Integer idpropAGGWT) throws SeditGwtException {
        try {
            PropositionAG prop = ucPropositionAG.calculClassementProposition(idpropAGGWT);
            return mapper.cloneToGwt(prop, PropositionAGGWT.class);
        } catch (BusinessException e) {
            if (SeditException.Type.MULTIPLE_EXCEPTION.equals(e.getType()))
                throw new SeditGwtException(e.getExceptionMap());
            else throw new SeditGwtException(SeditGwtException.TYPE_BUSINESS, e);
        } catch (TechnicalException e) {
            if (SeditException.Type.MULTIPLE_EXCEPTION.equals(e.getType()))
                throw new SeditGwtException(e.getExceptionMap());
            else throw new SeditGwtException(SeditGwtException.TYPE_TECHNICAL, e);
        } catch (Exception e) {
            throw new SeditGwtException(SeditGwtException.TYPE_INCONNUE, e);
        }
    }

    /**
     * @see fr.bl.client.grh.ca.avg.remote.IGwtServiceCaAvg#recalculClassementPropositionAG(java.util.List)
     */
    @Override
    public void recalculClassementPropositionAG(List<Integer> idpropAGGWT) throws SeditGwtException {
        try {
            ucPropositionAG.calculClassementProposition(idpropAGGWT);
        } catch (BusinessException e) {
            if (SeditException.Type.MULTIPLE_EXCEPTION.equals(e.getType()))
                throw new SeditGwtException(e.getExceptionMap());
            else throw new SeditGwtException(SeditGwtException.TYPE_BUSINESS, e);
        } catch (TechnicalException e) {
            if (SeditException.Type.MULTIPLE_EXCEPTION.equals(e.getType()))
                throw new SeditGwtException(e.getExceptionMap());
            else throw new SeditGwtException(SeditGwtException.TYPE_TECHNICAL, e);
        } catch (Exception e) {
            throw new SeditGwtException(SeditGwtException.TYPE_INCONNUE, e);
        }
    }

    /**
     * @see fr.bl.client.grh.ca.avg.remote.IGwtServiceCaAvg#recalculClassementPropositionAG(java.util.Map)
     */
    @Override
    public void recalculClassementPropositionAG(Map<String, Object> criteres) throws SeditGwtException {
        try {
            translateCriteria(criteres);
            ucPropositionAG.calculClassementProposition(criteres);
        } catch (BusinessException e) {
            if (SeditException.Type.MULTIPLE_EXCEPTION.equals(e.getType()))
                throw new SeditGwtException(e.getExceptionMap());
            else throw new SeditGwtException(SeditGwtException.TYPE_BUSINESS, e);
        } catch (TechnicalException e) {
            if (SeditException.Type.MULTIPLE_EXCEPTION.equals(e.getType()))
                throw new SeditGwtException(e.getExceptionMap());
            else throw new SeditGwtException(SeditGwtException.TYPE_TECHNICAL, e);
        } catch (Exception e) {
            throw new SeditGwtException(SeditGwtException.TYPE_INCONNUE, e);
        }
    }

    /**
     * @throws SeditGwtException
     * @see fr.bl.client.grh.ca.avg.remote.IGwtServiceCaAvg#findStatutByCriteria(HashMap, HashMap, long, long, String)
     */
    @Override
    public List<StatutDTOGWT> findStatutByCriteria(HashMap<String, Integer> tri, HashMap<String, Object> criteres, long firstLine, long limitLine, String filter) throws SeditGwtException {
        try {
            List<StatutDTO> list = ucDescriptionCAPAG.findStatutByCriteria(tri, criteres, firstLine, limitLine, filter);
            if (list == null) return new ArrayList<StatutDTOGWT>();
            return mapper.cloneToGwt(list, StatutDTOGWT.class);
        } catch (BusinessException e) {
            if (SeditException.Type.MULTIPLE_EXCEPTION.equals(e.getType()))
                throw new SeditGwtException(e.getExceptionMap());
            else throw new SeditGwtException(SeditGwtException.TYPE_BUSINESS, e);
        } catch (TechnicalException e) {
            if (SeditException.Type.MULTIPLE_EXCEPTION.equals(e.getType()))
                throw new SeditGwtException(e.getExceptionMap());
            else throw new SeditGwtException(SeditGwtException.TYPE_TECHNICAL, e);
        } catch (Exception e) {
            throw new SeditGwtException(SeditGwtException.TYPE_INCONNUE, e);
        }
    }

    /**
     * @throws SeditGwtException
     * @see fr.bl.client.grh.ca.avg.remote.IGwtServiceCaAvg#findCategorieByCadreStatutaireAndDate(String, Date)
     */
    @Override
    public List<CategorieDTOGWT> findCategorieDTOByCadreStatutaireAndDate(String idCadreStatutaire, Date date) throws SeditGwtException {
        try {
            List<CategorieDTO> list = this.ucDescriptionCAPAG.findCategorieDTOByCadreStatutaireAndDate(idCadreStatutaire, date);
            if (list == null) return new ArrayList<CategorieDTOGWT>();
            return mapper.cloneToGwt(list, CategorieDTOGWT.class);
        } catch (BusinessException e) {
            if (SeditException.Type.MULTIPLE_EXCEPTION.equals(e.getType()))
                throw new SeditGwtException(e.getExceptionMap());
            else throw new SeditGwtException(SeditGwtException.TYPE_BUSINESS, e);
        } catch (TechnicalException e) {
            if (SeditException.Type.MULTIPLE_EXCEPTION.equals(e.getType()))
                throw new SeditGwtException(e.getExceptionMap());
            else throw new SeditGwtException(SeditGwtException.TYPE_TECHNICAL, e);
        } catch (Exception e) {
            throw new SeditGwtException(SeditGwtException.TYPE_INCONNUE, e);
        }
    }

    /**
     * @see fr.bl.client.grh.ca.avg.remote.IGwtServiceCaAvg#findListFiliereDTOByCadreStatutaire(String)
     */
    @Override
    public List<FiliereDTOGWT> findListFiliereDTOByCadreStatutaireAndDate(String idCadreStatut, Date date) throws SeditGwtException {
        try {
            List<FiliereDTO> list = ucDescriptionCAPAG.findListFiliereDTOByCadreStatutaireAndDate(idCadreStatut, date);
            if (list == null) return new ArrayList<FiliereDTOGWT>();
            return mapper.cloneToGwt(list, FiliereDTOGWT.class);
        } catch (BusinessException e) {
            if (SeditException.Type.MULTIPLE_EXCEPTION.equals(e.getType()))
                throw new SeditGwtException(e.getExceptionMap());
            else throw new SeditGwtException(SeditGwtException.TYPE_BUSINESS, e);
        } catch (TechnicalException e) {
            if (SeditException.Type.MULTIPLE_EXCEPTION.equals(e.getType()))
                throw new SeditGwtException(e.getExceptionMap());
            else throw new SeditGwtException(SeditGwtException.TYPE_TECHNICAL, e);
        } catch (Exception e) {
            throw new SeditGwtException(SeditGwtException.TYPE_INCONNUE, e);
        }
    }

    /**
     * @throws SeditGwtException
     * @see IGwtServiceCaAvg#findListFiliereDTOAndCountPropositionByCriteresAndCadreStatutaireAndDate(Map, String, Date)
     */
    @Override
    public List<FiliereDTOGWT> findListFiliereDTOAndCountPropositionByCriteresAndCadreStatutaireAndDate(Map<String, Object> criteriaProposition, String idCadreStatut, Date date) throws SeditGwtException {
        try {
            translateCriteria(criteriaProposition);
            List<FiliereDTO> listFiliereDTO = ucVerificationPropositionAGCAP.findListFiliereDTOAndCountPropositionByCriteresAndCadreStatutaireAndDate(criteriaProposition, idCadreStatut, date);
            if (listFiliereDTO == null) return new ArrayList<FiliereDTOGWT>();
            List<FiliereDTOGWT> listFiliereDTOGWT = mapper.cloneToGwt(listFiliereDTO, FiliereDTOGWT.class);
            return listFiliereDTOGWT;
        } catch (BusinessException e) {
            if (SeditException.Type.MULTIPLE_EXCEPTION.equals(e.getType()))
                throw new SeditGwtException(e.getExceptionMap());
            else throw new SeditGwtException(SeditGwtException.TYPE_BUSINESS, e);
        } catch (TechnicalException e) {
            if (SeditException.Type.MULTIPLE_EXCEPTION.equals(e.getType()))
                throw new SeditGwtException(e.getExceptionMap());
            else throw new SeditGwtException(SeditGwtException.TYPE_TECHNICAL, e);
        } catch (Exception e) {
            throw new SeditGwtException(SeditGwtException.TYPE_INCONNUE, e);
        }
    }

    /**
     * @see fr.bl.client.grh.ca.avg.remote.IGwtServiceCaAvg#findListGroupeHierarchiqueDTOByCadreStatutaireAndDate(String, Date)
     */
    @Override
    public List<GroupeHierarchiqueDTOGWT> findListGroupeHierarchiqueDTOByCadreStatutaireAndDate(String idCadreStatut, Date date) throws SeditGwtException {
        try {
            List<GroupeHierarchiqueDTO> list = ucDescriptionCAPAG.findListGroupeHierarchiqueDTOByCadreStatutaireAndDate(idCadreStatut, date);
            if (list == null) return new ArrayList<GroupeHierarchiqueDTOGWT>();
            return mapper.cloneToGwt(list, GroupeHierarchiqueDTOGWT.class);
        } catch (BusinessException e) {
            if (SeditException.Type.MULTIPLE_EXCEPTION.equals(e.getType()))
                throw new SeditGwtException(e.getExceptionMap());
            else throw new SeditGwtException(SeditGwtException.TYPE_BUSINESS, e);
        } catch (TechnicalException e) {
            if (SeditException.Type.MULTIPLE_EXCEPTION.equals(e.getType()))
                throw new SeditGwtException(e.getExceptionMap());
            else throw new SeditGwtException(SeditGwtException.TYPE_TECHNICAL, e);
        } catch (Exception e) {
            throw new SeditGwtException(SeditGwtException.TYPE_INCONNUE, e);
        }
    }

    /**
     * @see fr.bl.client.grh.ca.avg.remote.IGwtServiceCaAvg#findListEcheancierCAPDTOByOrganisme(String)
     */
    @Override
    public List<EcheancierCAPDTOGWT> findListEcheancierCAPDTOByOrganisme(String idOrganisme) throws SeditGwtException {
        try {
            List<EcheancierCAPDTO> list = ucDescriptionCAPAG.findListEcheancierCAPDTOByOrganisme(idOrganisme);
            if (list == null) return new ArrayList<EcheancierCAPDTOGWT>();
            return mapper.cloneToGwt(list, EcheancierCAPDTOGWT.class);
        } catch (BusinessException e) {
            if (SeditException.Type.MULTIPLE_EXCEPTION.equals(e.getType()))
                throw new SeditGwtException(e.getExceptionMap());
            else throw new SeditGwtException(SeditGwtException.TYPE_BUSINESS, e);
        } catch (TechnicalException e) {
            if (SeditException.Type.MULTIPLE_EXCEPTION.equals(e.getType()))
                throw new SeditGwtException(e.getExceptionMap());
            else throw new SeditGwtException(SeditGwtException.TYPE_TECHNICAL, e);
        } catch (Exception e) {
            throw new SeditGwtException(SeditGwtException.TYPE_INCONNUE, e);
        }
    }

    /**
     * @see fr.bl.client.grh.ca.avg.remote.IGwtServiceCaAvg#loadListEcheancierCAPDTOByOrganismeAndTypeAvancement
     */
    @Override
    public List<EcheancierCAPDTOGWT> loadListEcheancierCAPDTOByOrganismeAndTypeAvancement(OrganismeGWT organisme, EnumTypeAEAGGWT type) throws SeditGwtException {
        try {
            Organisme orga = mapper.cloneToModel(organisme, Organisme.class);
            EnumTypeAEAG enumtype = mapper.cloneToModel(type, EnumTypeAEAG.class);

            List<EcheancierCAPDTO> list = ucDescriptionCAPAG.loadListEcheancierCAPDTOByOrganismeAndTypeAvancement(orga, enumtype);
            if (list == null) return new ArrayList<EcheancierCAPDTOGWT>();
            return mapper.cloneToGwt(list, EcheancierCAPDTOGWT.class);
        } catch (BusinessException e) {
            if (SeditException.Type.MULTIPLE_EXCEPTION.equals(e.getType()))
                throw new SeditGwtException(e.getExceptionMap());
            else throw new SeditGwtException(SeditGwtException.TYPE_BUSINESS, e);
        } catch (TechnicalException e) {
            if (SeditException.Type.MULTIPLE_EXCEPTION.equals(e.getType()))
                throw new SeditGwtException(e.getExceptionMap());
            else throw new SeditGwtException(SeditGwtException.TYPE_TECHNICAL, e);
        } catch (Exception e) {
            throw new SeditGwtException(SeditGwtException.TYPE_INCONNUE, e);
        }
    }


    /**
     * @see fr.bl.client.grh.ca.avg.remote.IGwtServiceCaAvg#findEcheanceCAPByEcheancierId(String)
     */
    @Override
    public List<EcheanceCAPGWT> findEcheanceCAPByEcheancierId(Integer echeancierCAPId) throws SeditGwtException {
        try {
            List<EcheanceCAP> listEcheance = ucDescriptionCAPAG.findEcheanceCAPByEcheancierId(echeancierCAPId);
            if (listEcheance == null) return new ArrayList<EcheanceCAPGWT>();
            List<EcheanceCAPGWT> listEcheanceGWT = mapper.cloneToGwt(listEcheance, EcheanceCAPGWT.class);
			/*EcheanceCAPGWT echeanceCAPGWT;
			// TODO utiliser mapper.cloneToGwt( liste, classe)
			for (Iterator it = listEcheance.iterator(); it.hasNext();) {
				EcheanceCAP echeance = (EcheanceCAP) it.next();
				echeance.setEcheancierCAP(null);
				echeanceCAPGWT = (EcheanceCAPGWT) mapper.cloneToGwt(echeance, EcheanceCAPGWT.class);
				listEcheanceGWT.add(echeanceCAPGWT);
			}*/
            return listEcheanceGWT;
        } catch (BusinessException e) {
            if (SeditException.Type.MULTIPLE_EXCEPTION.equals(e.getType()))
                throw new SeditGwtException(e.getExceptionMap());
            else throw new SeditGwtException(SeditGwtException.TYPE_BUSINESS, e);
        } catch (TechnicalException e) {
            if (SeditException.Type.MULTIPLE_EXCEPTION.equals(e.getType()))
                throw new SeditGwtException(e.getExceptionMap());
            else throw new SeditGwtException(SeditGwtException.TYPE_TECHNICAL, e);
        } catch (Exception e) {
            throw new SeditGwtException(SeditGwtException.TYPE_INCONNUE, e);
        }
    }

    /**
     * @see fr.bl.client.grh.ca.avg.remote.IGwtServiceCaAvg#saveAndReturnTableauAG(fr.bl.client.grh.coeur.sm.model.UtilisateurSMRHGWT, fr.bl.client.grh.coeur.ca.avg.model.TableauAGGWT, boolean)
     */
    @Override
    public TableauAGGWT saveAndReturnTableauAG(UtilisateurSMRHGWT utilisateurGWT, TableauAGGWT tableauAGGWT, boolean startEcheancier) throws SeditGwtException {
        return SeditGwtTry.get(() -> {
            TableauAG tableauAG = mapper.cloneToModel(tableauAGGWT, TableauAG.class);
            ContextSMRH test = mapper.cloneToModel(utilisateurGWT.getContext(), ContextSMRH.class);
            utilisateurGWT.setContext(null);
            UtilisateurSMRH utilisateur = mapper.cloneToModel(utilisateurGWT, UtilisateurSMRH.class);
            utilisateur.setContext(test);
            tableauAG = ucTableauAG.saveAndReturnTableauAG(utilisateur, tableauAG, startEcheancier);
            TableauAGGWT tableauGWTsaved = mapper.cloneToGwt(tableauAG, TableauAGGWT.class);
            return tableauGWTsaved;
        });
    }

    /**
     * @see fr.bl.client.grh.ca.avg.remote.IGwtServiceCaAvg#saveTableauAG(fr.bl.client.grh.coeur.ca.avg.model.TableauAGGWT)
     */
    @Override
    public void saveTableauAG(TableauAGGWT tableauAGGWT) throws SeditGwtException {
        try {
            TableauAG tableauAG = mapper.cloneToModel(tableauAGGWT, TableauAG.class);
            ucTableauAG.saveTableauAG(tableauAG);
        } catch (BusinessException e) {
            if (SeditException.Type.MULTIPLE_EXCEPTION.equals(e.getType()))
                throw new SeditGwtException(e.getExceptionMap());
            else throw new SeditGwtException(SeditGwtException.TYPE_BUSINESS, e);
        } catch (TechnicalException e) {
            if (SeditException.Type.MULTIPLE_EXCEPTION.equals(e.getType()))
                throw new SeditGwtException(e.getExceptionMap());
            else throw new SeditGwtException(SeditGwtException.TYPE_TECHNICAL, e);
        } catch (Exception e) {
            throw new SeditGwtException(SeditGwtException.TYPE_INCONNUE, e);
        }
    }

    /**
     * @see fr.bl.client.grh.ca.avg.remote.IGwtServiceCaAvg#findListFonctionAGDTOByCadreStatutaire(String)
     */
    @Override
    public List<FonctionAGDTOGWT> findListFonctionAGDTOByCadreStatutaire(String idCadreStatutaire) throws SeditGwtException {
        try {
            List<FonctionAGDTO> list = ucDescriptionCAPAG.findListFonctionAGDTOByCadreStatutaireId(idCadreStatutaire);
            if (list == null) return new ArrayList<FonctionAGDTOGWT>();
            return mapper.cloneToGwt(list, FonctionAGDTOGWT.class);
        } catch (BusinessException e) {
            if (SeditException.Type.MULTIPLE_EXCEPTION.equals(e.getType()))
                throw new SeditGwtException(e.getExceptionMap());
            else throw new SeditGwtException(SeditGwtException.TYPE_BUSINESS, e);
        } catch (TechnicalException e) {
            if (SeditException.Type.MULTIPLE_EXCEPTION.equals(e.getType()))
                throw new SeditGwtException(e.getExceptionMap());
            else throw new SeditGwtException(SeditGwtException.TYPE_TECHNICAL, e);
        } catch (Exception e) {
            throw new SeditGwtException(SeditGwtException.TYPE_INCONNUE, e);
        }
    }

    /**
     * @throws SeditGwtException
     * @see IGwtServiceCaAvg#findListParamPresentationCAPDTO(HashMap, HashMap, long, long, String)
     */
    @Override
    public List<ParamPresentationCAPDTOGWT> findListParamPresentationCAPDTO(Map<String, Integer> tri, Map<String, Object> criteria, long startIndex, long maxResult, String filter) throws SeditGwtException {
        try {
            if (criteria.containsKey("typeTableau")) {
                criteria.put("typeTableau", mapper.cloneToModel(criteria.get("typeTableau"), EnumTypeTableau.class));
            }
            if (criteria.containsKey("typePresentation")) {
                criteria.put("typePresentation", mapper.cloneToModel(criteria.get("typePresentation"), EnumTypePresentation.class));
            }
            List<ParamPresentationCAPDTO> list = this.ucDescriptionCAPAG.findListParamPresentationCAPDTO(tri, criteria, startIndex, maxResult, filter);
            if (list == null) 
                return new ArrayList<>();
            return mapper.cloneToGwt(list, ParamPresentationCAPDTOGWT.class);
        } catch (BusinessException e) {
            if (SeditException.Type.MULTIPLE_EXCEPTION.equals(e.getType()))
                throw new SeditGwtException(e.getExceptionMap());
            else throw new SeditGwtException(SeditGwtException.TYPE_BUSINESS, e);
        } catch (TechnicalException e) {
            if (SeditException.Type.MULTIPLE_EXCEPTION.equals(e.getType()))
                throw new SeditGwtException(e.getExceptionMap());
            else throw new SeditGwtException(SeditGwtException.TYPE_TECHNICAL, e);
        } catch (Exception e) {
            throw new SeditGwtException(SeditGwtException.TYPE_INCONNUE, e);
        }
    }

    /**
     * @throws SeditGwtException
     * @see IGwtServiceCaAvg#findParamCaOrgaComplete(String)
     */
    public ParamCaOrgaGWT findParamCaOrgaComplete(Organisme organisme) throws SeditGwtException {
        try {
            ParamCaOrga paramCaOrga = ucDescriptionCAPAG.findParamCaOrgaComplete(organisme.getId());
            return mapper.cloneToGwt(paramCaOrga, ParamCaOrgaGWT.class);
        } catch (BusinessException e) {
            if (SeditException.Type.MULTIPLE_EXCEPTION.equals(e.getType()))
                throw new SeditGwtException(e.getExceptionMap());
            else throw new SeditGwtException(SeditGwtException.TYPE_BUSINESS, e);
        } catch (TechnicalException e) {
            if (SeditException.Type.MULTIPLE_EXCEPTION.equals(e.getType()))
                throw new SeditGwtException(e.getExceptionMap());
            else throw new SeditGwtException(SeditGwtException.TYPE_TECHNICAL, e);
        } catch (Exception e) {
            throw new SeditGwtException(SeditGwtException.TYPE_INCONNUE, e);
        }
    }

    /**
     * @throws SeditGwtException
     * @see IGwtServiceCaAvg#loadCompleteTableauAGWithoutProposition(Integer)
     */
    @Override
    public TableauAGGWT loadCompleteTableauAGWithoutProposition(Integer tableauAGId) throws SeditGwtException {
        try {
            TableauAG tableauAG = ucTableauAG.loadCompleteTableauAGWithoutProposition(tableauAGId);
            TableauAGGWT tableauAGGWT = mapper.cloneToGwt(tableauAG, TableauAGGWT.class);
            return tableauAGGWT;
        } catch (BusinessException e) {
            if (SeditException.Type.MULTIPLE_EXCEPTION.equals(e.getType()))
                throw new SeditGwtException(e.getExceptionMap());
            else throw new SeditGwtException(SeditGwtException.TYPE_BUSINESS, e);
        } catch (TechnicalException e) {
            if (SeditException.Type.MULTIPLE_EXCEPTION.equals(e.getType()))
                throw new SeditGwtException(e.getExceptionMap());
            else throw new SeditGwtException(SeditGwtException.TYPE_TECHNICAL, e);
        } catch (Exception e) {
            throw new SeditGwtException(SeditGwtException.TYPE_INCONNUE, e);
        }
    }

    /**
     * @throws SeditGwtException
     * @see IGwtServiceCaAvg#loadCompleteTableauAGWithoutProposition(Integer)
     */
    @Override
    public Long countTableauAG(Map<String, Object> criteria, String filter) throws SeditGwtException {
        try {
            translateCriteria(criteria);
            EnumPhaseTableauAGGWT enumPhaseTableauAGGWT = (EnumPhaseTableauAGGWT) criteria.get("etatTableau<");
            if (enumPhaseTableauAGGWT != null) {
                criteria.put("etatTableau<", mapper.cloneToModel(enumPhaseTableauAGGWT, EnumPhaseTableauAG.class));
            }
            return ucTableauAG.countTableauAG(criteria, filter);
        } catch (BusinessException e) {
            if (SeditException.Type.MULTIPLE_EXCEPTION.equals(e.getType()))
                throw new SeditGwtException(e.getExceptionMap());
            else throw new SeditGwtException(SeditGwtException.TYPE_BUSINESS, e);
        } catch (TechnicalException e) {
            if (SeditException.Type.MULTIPLE_EXCEPTION.equals(e.getType()))
                throw new SeditGwtException(e.getExceptionMap());
            else throw new SeditGwtException(SeditGwtException.TYPE_TECHNICAL, e);
        } catch (Exception e) {
            throw new SeditGwtException(SeditGwtException.TYPE_INCONNUE, e);
        }
    }

    /**
     * @throws SeditGwtException
     * @see IGwtServiceCaAvg#deleteTableauAG(TableauAGGWT))
     */
    @Override
    public void deleteTableauAG(TableauAGGWT tableauAG) throws SeditGwtException {
        try {
            ucTableauAG.deleteTableauAG(mapper.cloneToModel(tableauAG, TableauAG.class));
        } catch (BusinessException e) {
            if (SeditException.Type.MULTIPLE_EXCEPTION.equals(e.getType()))
                throw new SeditGwtException(e.getExceptionMap());
            else throw new SeditGwtException(SeditGwtException.TYPE_BUSINESS, e);
        } catch (TechnicalException e) {
            if (SeditException.Type.MULTIPLE_EXCEPTION.equals(e.getType()))
                throw new SeditGwtException(e.getExceptionMap());
            else throw new SeditGwtException(SeditGwtException.TYPE_TECHNICAL, e);
        } catch (Exception e) {
            throw new SeditGwtException(SeditGwtException.TYPE_INCONNUE, e);
        }
    }

    /**
     * @throws SeditGwtException
     * @see IGwtServiceCaAvg#deleteListTableauAG(List<Integer>)
     */
    @Override
    public boolean deleteListTableauAG(List<Integer> listeIdTableaux) throws SeditGwtException {
        try {
            return ucTableauAG.deleteListTableauAG(listeIdTableaux);
        } catch (BusinessException e) {
            if (SeditException.Type.MULTIPLE_EXCEPTION.equals(e.getType()))
                throw new SeditGwtException(e.getExceptionMap());
            else throw new SeditGwtException(SeditGwtException.TYPE_BUSINESS, e);
        } catch (TechnicalException e) {
            if (SeditException.Type.MULTIPLE_EXCEPTION.equals(e.getType()))
                throw new SeditGwtException(e.getExceptionMap());
            else throw new SeditGwtException(SeditGwtException.TYPE_TECHNICAL, e);
        } catch (Exception e) {
            throw new SeditGwtException(SeditGwtException.TYPE_INCONNUE, e);
        }
    }


    private void translateCriteria(Map<String, Object> criteria) throws SeditGwtException {

        CollectiviteGWT collectivite = (CollectiviteGWT) criteria.get("collectivite");
        if (collectivite != null) {
            criteria.put("collectivite", mapper.cloneToModel(collectivite, Collectivite.class));
        }
		
		/*Object gwtOrganisme = criteria.get("organisme");
		if (gwtOrganisme != null)
			criteria.put("organisme", mapper.cloneToModel(gwtOrganisme, Organisme.class));
		*/
        if (criteria.containsKey("etatTableau")) {
            List<EnumPhaseTableauAGGWT> gwtEtatTableau = (List<EnumPhaseTableauAGGWT>) criteria.get("etatTableau");

            if (gwtEtatTableau != null) {
				/*if (gwtEtatTableau instanceof List) {
					List listeTmp = (List) gwtEtatTableau;
					String op1 = (String) listeTmp.remove(0);
					String op2 = (String) listeTmp.remove(0);
					List listeRetour = mapper.cloneToModel(listeTmp, EnumPhaseTableauAG.class);
					listeRetour.add(0, op1);
					listeRetour.add(1, op2);
					criteria.put("etatTableau", listeRetour);
				} else {*/
                criteria.put("etatTableau", mapper.cloneToModel(gwtEtatTableau, EnumPhaseTableauAG.class));
                /*}*/
            } else {
                Object obj = null;
                if (criteria.containsKey("!etatTableau")) {
                    obj = criteria.get("!etatTableau");
                    if (gwtEtatTableau != null)
                        criteria.put("!etatTableau", mapper.cloneToModel(gwtEtatTableau, EnumPhaseTableauAGGWT.class));
                }
            }
        }

        TableauAGGWT tableauAG = (TableauAGGWT) criteria.get("tableauAG");
        if (tableauAG != null) {
            criteria.put("tableauAG", mapper.cloneToModel(tableauAG, TableauAG.class));
        }
        EnumVerifierAGGWT enumVerifier = (EnumVerifierAGGWT) criteria.get("enumVerifier");
        if (enumVerifier != null) {
            criteria.put("enumVerifier", mapper.cloneToModel(enumVerifier, EnumVerifierAG.class));
        }
        EnumTypePropositionAGGWT enumTypePropositionAG = (EnumTypePropositionAGGWT) criteria.get("enumTypePropositionAG");
        if (enumTypePropositionAG != null) {
            criteria.put("enumTypePropositionAG", mapper.cloneToModel(enumTypePropositionAG, EnumTypePropositionAG.class));
        }
        enumTypePropositionAG = (EnumTypePropositionAGGWT) criteria.get("!enumTypePropositionAG");
        if (enumTypePropositionAG != null) {
            criteria.put("!enumTypePropositionAG", mapper.cloneToModel(enumTypePropositionAG, EnumTypePropositionAG.class));
        }
        EnumTypeInjectionAGGWT enumTypeInjectionAG = (EnumTypeInjectionAGGWT) criteria.get("enumTypeInjectionAG");
        if (enumTypeInjectionAG != null) {
            criteria.put("enumTypeInjectionAG", mapper.cloneToModel(enumTypeInjectionAG, EnumTypeInjectionAG.class));
        }
    }

    /**
     * @throws SeditGwtException
     * @see IGwtServiceCaAvg#findListTableauAG(Map<String, Integer> tri, Map<String, Object> criteria, long startIndex, long endIndex, String filter, boolean showMoreColumns)
     */
    @Override
    public List<List<Serializable>> findListTableauAG(Map<String, Integer> tri, Map<String, Object> criteria, long startIndex, long endIndex, String filter, boolean showMoreColumns) throws SeditGwtException {
        try {
            translateCriteria(criteria);
            EnumPhaseTableauAGGWT enumPhaseTableauAGGWT = (EnumPhaseTableauAGGWT) criteria.get("etatTableau<");
            if (enumPhaseTableauAGGWT != null) {
                criteria.put("etatTableau<", mapper.cloneToModel(enumPhaseTableauAGGWT, EnumPhaseTableauAG.class));
            }
            List<TableauAGDTO> serverList = ucTableauAG.findListTableauAG(tri, criteria, startIndex, endIndex, filter);
            if (serverList == null) return new ArrayList<List<Serializable>>();

            List<TableauAGDTOGWT> listTableauAGDTO = mapper.cloneToGwt(serverList, TableauAGDTOGWT.class);
            List<List<Serializable>> result = new ArrayList<List<Serializable>>();
            List<Serializable> innerList;
            IKey key;
            for (TableauAGDTOGWT capDTO : listTableauAGDTO) {
                innerList = new ArrayList<Serializable>();
                key = new Key();
                key.setId(capDTO.getId());
                key.setVersion(capDTO.getVersion());
                innerList.add(key);
                innerList.add(capDTO.getCode());
                innerList.add(capDTO.getLibelle());

                if (!showMoreColumns) {
                    innerList.add(capDTO.getEtatTableau());
                    innerList.add(capDTO);
                } else { // showMoreColumns -> Dans le cas du CRUD gestion des TableauAG on a besoin d'afficher + de colonnes.
                    // etat
                    EnumPhaseTableauAGGWT enumPhaseTableau = capDTO.getEtatTableau();
                    if (enumPhaseTableau == null) {
                        innerList.add("");
                    } else {
                        if (enumPhaseTableau.equals(EnumPhaseTableauAGGWT.INITIALISATION)) {
                            innerList.add("Brouillon");
                        } else if (enumPhaseTableau.equals(EnumPhaseTableauAGGWT.TABLEAU_CLOS)) {
                            innerList.add("Clos");
                        } else {
                            innerList.add("En cours");
                        }
                    }

                    // Type avancement de grade et type promotion interne
                    Boolean typeAG = capDTO.getTypeAG();
                    Boolean typePI = capDTO.getTypePI();
                    Boolean typeRE = capDTO.getTypeRE();

                    if (typeAG.booleanValue() && !typePI.booleanValue()) {
                        innerList.add(EnumTypeAvancementAGGWT.GRADE.getLibelle());
                    } else if (typePI.booleanValue() && !typeAG.booleanValue()) {
                        innerList.add(EnumTypeAvancementAGGWT.PROMOTION.getLibelle());
                    } else if (typePI.booleanValue() && typeAG.booleanValue()) {
                        innerList.add(EnumTypeAvancementAGGWT.GRADE.getLibelle() + " et " + EnumTypeAvancementAGGWT.PROMOTION.getLibelle());
                    } else if (typeRE.booleanValue()) {
                        innerList.add(EnumTypeAvancementAGGWT.RECLASSEMENT.getLibelle());
                    } else {
                        innerList.add("");
                    }

                    // période
                    String dateDeb = UtilsDate.dateToString(capDTO.getDateDebut());
                    String dateFin = UtilsDate.dateToString(capDTO.getDateFin());
                    innerList.add("Du " + dateDeb + " au " + dateFin);
                    // session
                    innerList.add(UtilsDate.dateToString(capDTO.getDateSession()));
                }

                result.add(innerList);
            }

            return result;
        } catch (BusinessException e) {
            if (SeditException.Type.MULTIPLE_EXCEPTION.equals(e.getType()))
                throw new SeditGwtException(e.getExceptionMap());
            else throw new SeditGwtException(SeditGwtException.TYPE_BUSINESS, e);
        } catch (TechnicalException e) {
            if (SeditException.Type.MULTIPLE_EXCEPTION.equals(e.getType()))
                throw new SeditGwtException(e.getExceptionMap());
            else throw new SeditGwtException(SeditGwtException.TYPE_TECHNICAL, e);
        } catch (Exception e) {
            throw new SeditGwtException(SeditGwtException.TYPE_INCONNUE, e);
        }
    }

    @Override
    public List<TableauAGGWT> findListTableauAGByIdOrganisme(String organismeId) {

        List<TableauAG> listTableauAGs = ucTableauAG.findListTableauAGByIdOrganisme(organismeId);
        if (listTableauAGs == null) return null;
        else {
            List<TableauAGGWT> listTableauAGGwt = mapper.cloneToGwt(listTableauAGs, TableauAGGWT.class);
            return listTableauAGGwt;
        }


    }


    @Override
    public TableauAGGWT findTableauAGById(Integer tableauId) throws SeditGwtException {
        try {
            TableauAG result = ucTableauAG.findTableauAGById(tableauId);
            if (result == null) return null;
            else return mapper.cloneToGwt(result, TableauAGGWT.class);
        } catch (BusinessException e) {
            if (SeditException.Type.MULTIPLE_EXCEPTION.equals(e.getType()))
                throw new SeditGwtException(e.getExceptionMap());
            else throw new SeditGwtException(SeditGwtException.TYPE_BUSINESS, e);
        } catch (TechnicalException e) {
            if (SeditException.Type.MULTIPLE_EXCEPTION.equals(e.getType()))
                throw new SeditGwtException(e.getExceptionMap());
            else throw new SeditGwtException(SeditGwtException.TYPE_TECHNICAL, e);
        } catch (Exception e) {
            throw new SeditGwtException(SeditGwtException.TYPE_INCONNUE, e);
        }
    }

    @Override
    public TableauAGGWT loadCompleteTableauAG(Integer tableauAGId) throws SeditGwtException {
        try {
            TableauAG result = ucTableauAG.loadCompleteTableauAG(tableauAGId);
            if (result == null) return null;
            else {
                // Probleme de mapping la regles Specifiques devient null cote gwt
                // alors qu'elle ne l'est pas coté serveur
                TableauAGGWT tableaugwt = mapper.cloneToGwt(result, TableauAGGWT.class);

                return tableaugwt;
            }
        } catch (BusinessException e) {
            if (SeditException.Type.MULTIPLE_EXCEPTION.equals(e.getType()))
                throw new SeditGwtException(e.getExceptionMap());
            else throw new SeditGwtException(SeditGwtException.TYPE_BUSINESS, e);
        } catch (TechnicalException e) {
            if (SeditException.Type.MULTIPLE_EXCEPTION.equals(e.getType()))
                throw new SeditGwtException(e.getExceptionMap());
            else throw new SeditGwtException(SeditGwtException.TYPE_TECHNICAL, e);
        } catch (Exception e) {
            throw new SeditGwtException(SeditGwtException.TYPE_INCONNUE, e);
        }
    }

    /**
     * @see fr.bl.client.grh.ca.avg.remote.IGwtServiceCaAvg#findParamCaOrgaComplete(fr.bl.client.grh.coeur.cs.model.parametrage.OrganismeGWT)
     */
    @Override
    public ParamCaOrgaGWT findParamCaOrgaComplete(String idOrganisme) throws SeditGwtException {
        try {
            ParamCaOrga paramCaOrga = ucDescriptionCAPAG.findParamCaOrgaComplete(idOrganisme);
            ParamCaOrgaGWT paramCaOrgaGWT = mapper.cloneToGwt(paramCaOrga, ParamCaOrgaGWT.class);
            return paramCaOrgaGWT;
        } catch (BusinessException e) {
            if (SeditException.Type.MULTIPLE_EXCEPTION.equals(e.getType()))
                throw new SeditGwtException(e.getExceptionMap());
            else throw new SeditGwtException(SeditGwtException.TYPE_BUSINESS, e);
        } catch (TechnicalException e) {
            if (SeditException.Type.MULTIPLE_EXCEPTION.equals(e.getType()))
                throw new SeditGwtException(e.getExceptionMap());
            else throw new SeditGwtException(SeditGwtException.TYPE_TECHNICAL, e);
        } catch (Exception e) {
            throw new SeditGwtException(SeditGwtException.TYPE_INCONNUE, e);
        }
    }

    /**
     * @throws SeditGwtException
     * @see IGwtServiceCaAvg#findListCadreEmploiDTOByFiliere(String, Date)
     */
    @Override
    public List<CadreEmploiDTOGWT> findListCadreEmploiDTOByFiliere(String idFiliere, Date date) throws SeditGwtException {
        try {
            List<CadreEmploiDTO> listCadreEmploiDTO = ucVerificationParametrageCAP.findListCadreEmploiDTOByFiliere(idFiliere, date);
            if (listCadreEmploiDTO == null) return new ArrayList<CadreEmploiDTOGWT>();
            List<CadreEmploiDTOGWT> listCadreEmploiDTOGWT = mapper.cloneToGwt(listCadreEmploiDTO, CadreEmploiDTOGWT.class);
            return listCadreEmploiDTOGWT;
        } catch (BusinessException e) {
            if (SeditException.Type.MULTIPLE_EXCEPTION.equals(e.getType()))
                throw new SeditGwtException(e.getExceptionMap());
            else throw new SeditGwtException(SeditGwtException.TYPE_BUSINESS, e);
        } catch (TechnicalException e) {
            if (SeditException.Type.MULTIPLE_EXCEPTION.equals(e.getType()))
                throw new SeditGwtException(e.getExceptionMap());
            else throw new SeditGwtException(SeditGwtException.TYPE_TECHNICAL, e);
        } catch (Exception e) {
            throw new SeditGwtException(SeditGwtException.TYPE_INCONNUE, e);
        }
    }

    /**
     * @throws SeditGwtException
     * @see IGwtServiceCaAvg#findListCadreEmploiDTOByFiliere(String, Date)
     */
    @Override
    public List<CadreEmploiDTOGWT> findListCadreEmploiDTOAndCountPropositionByCriteresAndFiliere(Map<String, Object> criteriaProposition, String idFiliere, Date date) throws SeditGwtException {
        try {
            translateCriteria(criteriaProposition);
            List<CadreEmploiDTO> listCadreEmploiDTO = ucVerificationPropositionAGCAP.findListCadreEmploiDTOAndCountPropositionByCriteresAndFiliere(criteriaProposition, idFiliere, date);
            if (listCadreEmploiDTO == null) return new ArrayList<CadreEmploiDTOGWT>();
            List<CadreEmploiDTOGWT> listCadreEmploiDTOGWT = mapper.cloneToGwt(listCadreEmploiDTO, CadreEmploiDTOGWT.class);
            return listCadreEmploiDTOGWT;
        } catch (BusinessException e) {
            if (SeditException.Type.MULTIPLE_EXCEPTION.equals(e.getType()))
                throw new SeditGwtException(e.getExceptionMap());
            else throw new SeditGwtException(SeditGwtException.TYPE_BUSINESS, e);
        } catch (TechnicalException e) {
            if (SeditException.Type.MULTIPLE_EXCEPTION.equals(e.getType()))
                throw new SeditGwtException(e.getExceptionMap());
            else throw new SeditGwtException(SeditGwtException.TYPE_TECHNICAL, e);
        } catch (Exception e) {
            throw new SeditGwtException(SeditGwtException.TYPE_INCONNUE, e);
        }
    }

    /**
     * @throws SeditGwtException
     * @see {@link IGwtServiceCaAvg#findListRatiosParTableauAG(Map, Map, long, long, String)}
     */
    @Override
    public List<RatiosQuotasParTableauAGDTOGWT> findListRatiosQuotasParTableauAGDTO(Map<String, Integer> tri, Map<String, Object> criteria, long startIndex, long endIndex, String filter) throws SeditGwtException {
        try {
            List<RatiosQuotasParTableauAGDTO> listRatiosParTableauAGDTO = ucRatiosQuotasParTableau.findListRatiosQuotasParTableauAGDTO(tri, criteria, startIndex, endIndex, filter);
            if (listRatiosParTableauAGDTO == null) return new ArrayList<RatiosQuotasParTableauAGDTOGWT>();
            List<RatiosQuotasParTableauAGDTOGWT> listRatiosParTableauAGDTOGWT = mapper.cloneToGwt(listRatiosParTableauAGDTO, RatiosQuotasParTableauAGDTOGWT.class);

            return listRatiosParTableauAGDTOGWT;
        } catch (BusinessException e) {
            if (SeditException.Type.MULTIPLE_EXCEPTION.equals(e.getType()))
                throw new SeditGwtException(e.getExceptionMap());
            else throw new SeditGwtException(SeditGwtException.TYPE_BUSINESS, e);
        } catch (TechnicalException e) {
            if (SeditException.Type.MULTIPLE_EXCEPTION.equals(e.getType()))
                throw new SeditGwtException(e.getExceptionMap());
            else throw new SeditGwtException(SeditGwtException.TYPE_TECHNICAL, e);
        } catch (Exception e) {
            throw new SeditGwtException(SeditGwtException.TYPE_INCONNUE, e);
        }
    }

    /**
     * @throws SeditGwtException
     * @see {@link IGwtServiceCaAvg#countRatiosQuotasParTableauAG(Map, String)}
     */
    @Override
    public Long countRatiosQuotasParTableauAG(Map<String, Object> criteria, String filter) throws SeditGwtException {
        try {
            return ucRatiosQuotasParTableau.countRatiosQuotasParTableauAG(criteria, filter);
        } catch (BusinessException e) {
            if (SeditException.Type.MULTIPLE_EXCEPTION.equals(e.getType()))
                throw new SeditGwtException(e.getExceptionMap());
            else throw new SeditGwtException(SeditGwtException.TYPE_BUSINESS, e);
        } catch (TechnicalException e) {
            if (SeditException.Type.MULTIPLE_EXCEPTION.equals(e.getType()))
                throw new SeditGwtException(e.getExceptionMap());
            else throw new SeditGwtException(SeditGwtException.TYPE_TECHNICAL, e);
        } catch (Exception e) {
            throw new SeditGwtException(SeditGwtException.TYPE_INCONNUE, e);
        }
    }


    /**
     * @throws SeditGwtException
     * @see {@link IGwtServiceCaAvg#purgerNonPromouvablesDesTableaux(List, boolean, boolean, boolean)}
     */
    @Override
    public void purgerNonPromouvablesDesTableaux(List<TableauAGGWT> listeTableaux, boolean supprPropositions, boolean supprAvis, boolean supprConditions) throws SeditGwtException {
        try {
            ucTableauAG.purgerNonPromouvablesDesTableaux(mapper.cloneToModel(listeTableaux, TableauAG.class), supprPropositions, supprAvis, supprConditions);
        } catch (BusinessException e) {
            if (SeditException.Type.MULTIPLE_EXCEPTION.equals(e.getType()))
                throw new SeditGwtException(e.getExceptionMap());
            else throw new SeditGwtException(SeditGwtException.TYPE_BUSINESS, e);
        } catch (TechnicalException e) {
            if (SeditException.Type.MULTIPLE_EXCEPTION.equals(e.getType()))
                throw new SeditGwtException(e.getExceptionMap());
            else throw new SeditGwtException(SeditGwtException.TYPE_TECHNICAL, e);
        } catch (Exception e) {
            throw new SeditGwtException(SeditGwtException.TYPE_INCONNUE, e);
        }
    }

    /**
     * @throws SeditGwtException
     * @see {@link IGwtServiceCaAvg#purgerNonPromusDesTableaux(List, boolean, boolean, boolean)}
     */
    @Override
    public void purgerNonPromusDesTableaux(List<TableauAGGWT> listeTableaux, boolean supprPropositions, boolean supprAvis, boolean supprConditions) throws SeditGwtException {
        try {
            ucTableauAG.purgerNonPromusDesTableaux(mapper.cloneToModel(listeTableaux, TableauAG.class), supprPropositions, supprAvis, supprConditions);
        } catch (BusinessException e) {
            if (SeditException.Type.MULTIPLE_EXCEPTION.equals(e.getType()))
                throw new SeditGwtException(e.getExceptionMap());
            else throw new SeditGwtException(SeditGwtException.TYPE_BUSINESS, e);
        } catch (TechnicalException e) {
            if (SeditException.Type.MULTIPLE_EXCEPTION.equals(e.getType()))
                throw new SeditGwtException(e.getExceptionMap());
            else throw new SeditGwtException(SeditGwtException.TYPE_TECHNICAL, e);
        } catch (Exception e) {
            throw new SeditGwtException(SeditGwtException.TYPE_INCONNUE, e);
        }
    }

    /**
     * @throws SeditGwtException
     * @see {@link IGwtServiceCaAvg#purgerPromusDesTableaux(List, boolean, boolean, boolean)}
     */
    @Override
    public void purgerPromusDesTableaux(List<TableauAGGWT> listeTableaux, boolean supprPropositions, boolean supprAvis, boolean supprConditions) throws SeditGwtException {
        try {
            ucTableauAG.purgerPromusDesTableaux(mapper.cloneToModel(listeTableaux, TableauAG.class), supprPropositions, supprAvis, supprConditions);
        } catch (BusinessException e) {
            if (SeditException.Type.MULTIPLE_EXCEPTION.equals(e.getType()))
                throw new SeditGwtException(e.getExceptionMap());
            else throw new SeditGwtException(SeditGwtException.TYPE_BUSINESS, e);
        } catch (TechnicalException e) {
            if (SeditException.Type.MULTIPLE_EXCEPTION.equals(e.getType()))
                throw new SeditGwtException(e.getExceptionMap());
            else throw new SeditGwtException(SeditGwtException.TYPE_TECHNICAL, e);
        } catch (Exception e) {
            throw new SeditGwtException(SeditGwtException.TYPE_INCONNUE, e);
        }
    }

    /**
     * @throws SeditGwtException
     * @see {@link IGwtServiceCaAvg#loadCompleteRatiosQuotasParTableauById(Integer)}
     */
    @Override
    public RatiosQuotasParTableauGWT loadCompleteRatiosQuotasParTableauById(Integer ratiosQuotasParTableauId) throws SeditGwtException {
        try {
            RatiosQuotasParTableau result = ucRatiosQuotasParTableau.loadCompleteRatiosQuotasParTableauById(ratiosQuotasParTableauId);
            RatiosQuotasParTableauGWT resultGWT = mapper.cloneToGwt(result, RatiosQuotasParTableauGWT.class);
            return resultGWT;
        } catch (BusinessException e) {
            if (SeditException.Type.MULTIPLE_EXCEPTION.equals(e.getType()))
                throw new SeditGwtException(e.getExceptionMap());
            else throw new SeditGwtException(SeditGwtException.TYPE_BUSINESS, e);
        } catch (TechnicalException e) {
            if (SeditException.Type.MULTIPLE_EXCEPTION.equals(e.getType()))
                throw new SeditGwtException(e.getExceptionMap());
            else throw new SeditGwtException(SeditGwtException.TYPE_TECHNICAL, e);
        } catch (Exception e) {
            throw new SeditGwtException(SeditGwtException.TYPE_INCONNUE, e);
        }
    }

    /**
     * @throws SeditGwtException
     * @see {@link IGwtServiceCaAvg#saveRatiosQuotasParTableau(RatiosQuotasParTableauGWT)}
     */
    @Override
    public RatiosQuotasParTableauGWT saveRatiosQuotasParTableau(RatiosQuotasParTableauGWT ratiosQuotasParTableau) throws SeditGwtException {
        try {
            RatiosQuotasParTableau ratio = ucRatiosQuotasParTableau.saveRatiosQuotasParTableau(mapper.cloneToModel(ratiosQuotasParTableau, RatiosQuotasParTableau.class));
            RatiosQuotasParTableauGWT ratioGWT = mapper.cloneToGwt(ratio, RatiosQuotasParTableauGWT.class);
            return ratioGWT;
        } catch (BusinessException e) {
            if (SeditException.Type.MULTIPLE_EXCEPTION.equals(e.getType()))
                throw new SeditGwtException(e.getExceptionMap());
            else throw new SeditGwtException(SeditGwtException.TYPE_BUSINESS, e);
        } catch (TechnicalException e) {
            if (SeditException.Type.MULTIPLE_EXCEPTION.equals(e.getType()))
                throw new SeditGwtException(e.getExceptionMap());
            else throw new SeditGwtException(SeditGwtException.TYPE_TECHNICAL, e);
        } catch (Exception e) {
            throw new SeditGwtException(SeditGwtException.TYPE_INCONNUE, e);
        }
    }

    /**
     * @throws SeditGwtException
     * @see {@link IGwtServiceCaAvg#exportRatiosQuotasParTableauForExcel(Map, Map, String)}
     */
    @Override
    public List<List<String>> exportRatiosQuotasParTableauForExcel(Map<String, Integer> tri, Map<String, Object> criteres, String filter) throws SeditGwtException {
        try {
            translateCriteria(criteres);
            return ucRatiosQuotasParTableau.exportRatiosQuotasParTableauForExcel(tri, criteres, filter);
        } catch (BusinessException e) {
            if (SeditException.Type.MULTIPLE_EXCEPTION.equals(e.getType()))
                throw new SeditGwtException(e.getExceptionMap());
            else throw new SeditGwtException(SeditGwtException.TYPE_BUSINESS, e);
        } catch (TechnicalException e) {
            if (SeditException.Type.MULTIPLE_EXCEPTION.equals(e.getType()))
                throw new SeditGwtException(e.getExceptionMap());
            else throw new SeditGwtException(SeditGwtException.TYPE_TECHNICAL, e);
        } catch (Exception e) {
            throw new SeditGwtException(SeditGwtException.TYPE_INCONNUE, e);
        }
    }

    /**
     * @see fr.bl.client.grh.ca.avg.remote.IGwtServiceCaAvg#findListGradeDTOByCadreStatutaire(String)
     */
    @Override
    public List<GradeDTOGWT> findListeGradeDTOByCadreEmploi(String idCadreEmploi) throws SeditGwtException {
        try {
            List<GradeDTO> list = ucVerificationPropositionAGCAP.findListeGradeDTOByCadreEmploi(idCadreEmploi);
            if (list == null) return new ArrayList<GradeDTOGWT>();
            return mapper.cloneToGwt(list, GradeDTOGWT.class);
        } catch (BusinessException e) {
            if (SeditException.Type.MULTIPLE_EXCEPTION.equals(e.getType()))
                throw new SeditGwtException(e.getExceptionMap());
            else throw new SeditGwtException(SeditGwtException.TYPE_BUSINESS, e);
        } catch (TechnicalException e) {
            if (SeditException.Type.MULTIPLE_EXCEPTION.equals(e.getType()))
                throw new SeditGwtException(e.getExceptionMap());
            else throw new SeditGwtException(SeditGwtException.TYPE_TECHNICAL, e);
        } catch (Exception e) {
            throw new SeditGwtException(SeditGwtException.TYPE_INCONNUE, e);
        }
    }

    /**
     * @see fr.bl.client.grh.ca.avg.remote.IGwtServiceCaAvg#findListFiliereDTOByCadreStatutaire(String)
     */
    @Override
    public List<GradeDTOGWT> findListeGradeDTOAndCountPropositionByCriteresAndCadreEmploi(Map<String, Object> criteriaProposition, String idCadreEmploi) throws SeditGwtException {
        try {
            translateCriteria(criteriaProposition);
            List<GradeDTO> list = ucVerificationPropositionAGCAP.findListeGradeDTOAndCountPropositionByCriteresAndCadreEmploi(criteriaProposition, idCadreEmploi);
            if (list == null) return new ArrayList<GradeDTOGWT>();
            return mapper.cloneToGwt(list, GradeDTOGWT.class);
        } catch (BusinessException e) {
            if (SeditException.Type.MULTIPLE_EXCEPTION.equals(e.getType()))
                throw new SeditGwtException(e.getExceptionMap());
            else throw new SeditGwtException(SeditGwtException.TYPE_BUSINESS, e);
        } catch (TechnicalException e) {
            if (SeditException.Type.MULTIPLE_EXCEPTION.equals(e.getType()))
                throw new SeditGwtException(e.getExceptionMap());
            else throw new SeditGwtException(SeditGwtException.TYPE_TECHNICAL, e);
        } catch (Exception e) {
            throw new SeditGwtException(SeditGwtException.TYPE_INCONNUE, e);
        }
    }

    /**
     * @throws SeditGwtException
     * @see {@link IGwtServiceCaAvg#loadCompleteRatiosQuotasParTableauByTableauAGAndGrade(TableauAGGWT, GradeGWT)}
     */
    @Override
    public RatiosQuotasParTableauGWT loadCompleteRatiosQuotasParTableauByTableauAGAndGrade(TableauAGGWT tableauAGGWT, GradeGWT gradeGWT) throws SeditGwtException {
        try {
            TableauAG tableau = mapper.cloneToModel(tableauAGGWT, TableauAG.class);
            Grade grade = mapper.cloneToModel(gradeGWT, Grade.class);
            RatiosQuotasParTableau result = ucRatiosQuotasParTableau.loadCompleteRatiosQuotasParTableauByTableauAGAndGrade(tableau, grade);
            RatiosQuotasParTableauGWT resultGWT = mapper.cloneToGwt(result, RatiosQuotasParTableauGWT.class);
            return resultGWT;
        } catch (BusinessException e) {
            if (SeditException.Type.MULTIPLE_EXCEPTION.equals(e.getType()))
                throw new SeditGwtException(e.getExceptionMap());
            else throw new SeditGwtException(SeditGwtException.TYPE_BUSINESS, e);
        } catch (TechnicalException e) {
            if (SeditException.Type.MULTIPLE_EXCEPTION.equals(e.getType()))
                throw new SeditGwtException(e.getExceptionMap());
            else throw new SeditGwtException(SeditGwtException.TYPE_TECHNICAL, e);
        } catch (Exception e) {
            throw new SeditGwtException(SeditGwtException.TYPE_INCONNUE, e);
        }
    }

    /**
     * @throws SeditGwtException
     * @see {@link IGwtServiceCaAvg#findGradeById(String)}
     */
    @Override
    public GradeGWT findGradeById(String id) throws SeditGwtException {
        try {
            Grade grade = ucGrade.findGradeById(id);
            return mapper.cloneToGwt(grade, GradeGWT.class);
        } catch (BusinessException e) {
            if (SeditException.Type.MULTIPLE_EXCEPTION.equals(e.getType()))
                throw new SeditGwtException(e.getExceptionMap());
            else throw new SeditGwtException(SeditGwtException.TYPE_BUSINESS, e);
        } catch (TechnicalException e) {
            if (SeditException.Type.MULTIPLE_EXCEPTION.equals(e.getType()))
                throw new SeditGwtException(e.getExceptionMap());
            else throw new SeditGwtException(SeditGwtException.TYPE_TECHNICAL, e);
        } catch (Exception e) {
            throw new SeditGwtException(SeditGwtException.TYPE_INCONNUE, e);
        }
    }

    /**
     * @throws SeditGwtException
     * @see {@link IGwtServiceCaAvg#countPropositionWithCriteria(Map, String)}
     */
    @Override
    public Long countPropositionWithCriteria(Map<String, Object> criteria, String filter) throws SeditGwtException {
        try {
            translateCriteria(criteria);
            return ucVerificationPropositionAGCAP.countPropositionWithCriteria(criteria, filter);
        } catch (BusinessException e) {
            if (SeditException.Type.MULTIPLE_EXCEPTION.equals(e.getType()))
                throw new SeditGwtException(e.getExceptionMap());
            else throw new SeditGwtException(SeditGwtException.TYPE_BUSINESS, e);
        } catch (TechnicalException e) {
            if (SeditException.Type.MULTIPLE_EXCEPTION.equals(e.getType()))
                throw new SeditGwtException(e.getExceptionMap());
            else throw new SeditGwtException(SeditGwtException.TYPE_TECHNICAL, e);
        } catch (Exception e) {
            throw new SeditGwtException(SeditGwtException.TYPE_INCONNUE, e);
        }
    }

    /**
     * @throws SeditGwtException
     * @see {@link IGwtServiceCaAvg#findListPropositionAGDTO(Map, Map, long, long, String)}
     */
    @Override
    public List<PropositionAGDTOGWT> findListPropositionAGDTO(Map<String, Integer> tri, Map<String, Object> criteres, long firstLine, long limitLine, String filter) throws SeditGwtException {
        try {
            translateCriteria(criteres);
            log.debug("Avant mapping des propositions");
            Long deb = System.currentTimeMillis();
            List<PropositionAGDTO> list = ucVerificationPropositionAGCAP.findListPropositionAGDTO(tri, criteres, firstLine, limitLine, filter);
            log.debug("Temps requete=" + (System.currentTimeMillis() - deb));
            deb = System.currentTimeMillis();
            List<PropositionAGDTOGWT> retour = mapper.cloneToGwt(list, PropositionAGDTOGWT.class);
            log.debug("Temps mapping=" + (System.currentTimeMillis() - deb));
            return retour;
        } catch (BusinessException e) {
            if (SeditException.Type.MULTIPLE_EXCEPTION.equals(e.getType()))
                throw new SeditGwtException(e.getExceptionMap());
            else throw new SeditGwtException(SeditGwtException.TYPE_BUSINESS, e);
        } catch (TechnicalException e) {
            if (SeditException.Type.MULTIPLE_EXCEPTION.equals(e.getType()))
                throw new SeditGwtException(e.getExceptionMap());
            else throw new SeditGwtException(SeditGwtException.TYPE_TECHNICAL, e);
        } catch (Exception e) {
            throw new SeditGwtException(SeditGwtException.TYPE_INCONNUE, e);
        }
    }

    /**
     * @throws SeditGwtException
     * @see {@link IGwtServiceCaAvg#findParamRangClassementAGByOrganisme(OrganismeGWT)}
     */
    @Override
    public ParamRangClassementAGGWT findParamRangClassementAGByOrganisme(OrganismeGWT organismeGWT) throws SeditGwtException {
        try {
            Organisme orga = mapper.cloneToModel(organismeGWT, Organisme.class);
            ParamRangClassementAG param = ucVerificationPropositionAGCAP.findParamRangClassementAGByOrganisme(orga);
            if (param != null) {
                return mapper.cloneToGwt(param, ParamRangClassementAGGWT.class);
            } else return null;
        } catch (BusinessException e) {
            if (SeditException.Type.MULTIPLE_EXCEPTION.equals(e.getType()))
                throw new SeditGwtException(e.getExceptionMap());
            else throw new SeditGwtException(SeditGwtException.TYPE_BUSINESS, e);
        } catch (TechnicalException e) {
            if (SeditException.Type.MULTIPLE_EXCEPTION.equals(e.getType()))
                throw new SeditGwtException(e.getExceptionMap());
            else throw new SeditGwtException(SeditGwtException.TYPE_TECHNICAL, e);
        } catch (Exception e) {
            throw new SeditGwtException(SeditGwtException.TYPE_INCONNUE, e);
        }
    }

    /**
     * @throws SeditGwtException
     * @see {@link IGwtServiceCaAvg#findListValeurClassementAGByParamRangClassementAG(ParamRangClassementAGGWT)}
     */
    @Override
    public List<ValeurClassementAGGWT> findListValeurClassementAGByParamRangClassementAG(ParamRangClassementAGGWT param) throws SeditGwtException {
        try {
            List<ValeurClassementAG> listResult = ucVerificationPropositionAGCAP.findListValeurClassementAGByParamRangClassementAG(mapper.cloneToModel(param, ParamRangClassementAG.class));
            List<ValeurClassementAGGWT> listResultGwt = mapper.cloneToGwt(listResult, ValeurClassementAGGWT.class);
            Collections.sort(listResultGwt, new ValeurClassementAGGWT.ComparatorValeurAsc());

            return listResultGwt;
        } catch (BusinessException e) {
            if (SeditException.Type.MULTIPLE_EXCEPTION.equals(e.getType()))
                throw new SeditGwtException(e.getExceptionMap());
            else throw new SeditGwtException(SeditGwtException.TYPE_BUSINESS, e);
        } catch (TechnicalException e) {
            if (SeditException.Type.MULTIPLE_EXCEPTION.equals(e.getType()))
                throw new SeditGwtException(e.getExceptionMap());
            else throw new SeditGwtException(SeditGwtException.TYPE_TECHNICAL, e);
        } catch (Exception e) {
            throw new SeditGwtException(SeditGwtException.TYPE_INCONNUE, e);
        }
    }

    /**
     * @throws SeditGwtException
     * @see {@link IGwtServiceCaAvg#changeVerifieePropositionAG(Integer, EnumVerifierAGGWT)}
     */
    @Override
    public void changeVerifieePropositionAG(Integer propositionId, EnumVerifierAGGWT enumVerifierGWT) throws SeditGwtException {
        try {
            EnumVerifierAG enumVerifier = mapper.cloneToModel(enumVerifierGWT, EnumVerifierAG.class);
            ucVerificationPropositionAGCAP.changeVerifieePropositionAG(propositionId, enumVerifier);
        } catch (BusinessException e) {
            if (SeditException.Type.MULTIPLE_EXCEPTION.equals(e.getType()))
                throw new SeditGwtException(e.getExceptionMap());
            else throw new SeditGwtException(SeditGwtException.TYPE_BUSINESS, e);
        } catch (TechnicalException e) {
            if (SeditException.Type.MULTIPLE_EXCEPTION.equals(e.getType()))
                throw new SeditGwtException(e.getExceptionMap());
            else throw new SeditGwtException(SeditGwtException.TYPE_TECHNICAL, e);
        } catch (Exception e) {
            throw new SeditGwtException(SeditGwtException.TYPE_INCONNUE, e);
        }
    }

    /**
     * @throws SeditGwtException
     * @see {@link IGwtServiceCaAvg#changeVerifieeListPropositionAG(List)}
     */
    @Override
    public List<String[]> changeVerifieeListPropositionAG(List<String[]> listTablePropositionIdAndBooleanVerifiee) throws SeditGwtException {
        return SeditGwtTry.get(() -> {
            ucVerificationPropositionAGCAP.changeVerifieeListPropositionAG(listTablePropositionIdAndBooleanVerifiee);
            return listTablePropositionIdAndBooleanVerifiee;
        });
    }


    /**
     * @throws SeditGwtException
     * @see {@link IGwtServiceCaAvg#changeRangClassementOfPropositionAG(Integer, String, String)}
     */
    @Override
    public void changeRangClassementOfPropositionAG(Integer propositionId, String saisieLibre, String valeur) throws SeditGwtException {
        try {
            ucVerificationPropositionAGCAP.changeRangClassementOfPropositionAG(propositionId, saisieLibre, valeur);
        } catch (BusinessException e) {
            if (SeditException.Type.MULTIPLE_EXCEPTION.equals(e.getType()))
                throw new SeditGwtException(e.getExceptionMap());
            else throw new SeditGwtException(SeditGwtException.TYPE_BUSINESS, e);
        } catch (TechnicalException e) {
            if (SeditException.Type.MULTIPLE_EXCEPTION.equals(e.getType()))
                throw new SeditGwtException(e.getExceptionMap());
            else throw new SeditGwtException(SeditGwtException.TYPE_TECHNICAL, e);
        } catch (Exception e) {
            throw new SeditGwtException(SeditGwtException.TYPE_INCONNUE, e);
        }
    }

    /**
     * @throws SeditGwtException
     * @see {@link IGwtServiceCaAvg#exclureReintegrerPropositionAG(Integer, boolean)}
     */
    @Override
    public void exclureReintegrerPropositionAG(Integer propositionId, boolean promouvable) throws SeditGwtException {
        try {
            ucVerificationPropositionAGCAP.exclureReintegrerPropositionAG(propositionId, promouvable);
        } catch (BusinessException e) {
            if (SeditException.Type.MULTIPLE_EXCEPTION.equals(e.getType()))
                throw new SeditGwtException(e.getExceptionMap());
            else throw new SeditGwtException(SeditGwtException.TYPE_BUSINESS, e);
        } catch (TechnicalException e) {
            if (SeditException.Type.MULTIPLE_EXCEPTION.equals(e.getType()))
                throw new SeditGwtException(e.getExceptionMap());
            else throw new SeditGwtException(SeditGwtException.TYPE_TECHNICAL, e);
        } catch (Exception e) {
            throw new SeditGwtException(SeditGwtException.TYPE_INCONNUE, e);
        }
    }

    @Override
    public void exclureListPropositionAG(List<Integer> listPropositionsId) throws SeditGwtException {
        try {
            for (Integer propositionId : listPropositionsId) {
                this.ucPropositionAG.exclureProposition(propositionId);
            }
        } catch (BusinessException e) {
            if (SeditException.Type.MULTIPLE_EXCEPTION.equals(e.getType()))
                throw new SeditGwtException(e.getExceptionMap());
            else throw new SeditGwtException(SeditGwtException.TYPE_BUSINESS, e);
        } catch (TechnicalException e) {
            if (SeditException.Type.MULTIPLE_EXCEPTION.equals(e.getType()))
                throw new SeditGwtException(e.getExceptionMap());
            else throw new SeditGwtException(SeditGwtException.TYPE_TECHNICAL, e);
        } catch (Exception e) {
            throw new SeditGwtException(SeditGwtException.TYPE_INCONNUE, e);
        }
    }

    /**
     * @throws SeditGwtException
     * @see {@link IGwtServiceCaAvg#findListPropositionNonPromouvableAGDTO(Map, Map, long, long, String))}
     */
    @Override
    public List<PropositionNonPromouvableAGDTOGWT> findListPropositionNonPromouvableAGDTO(Map<String, Integer> tri, Map<String, Object> criteres, long firstLine, long limitLine, String filter) throws SeditGwtException {
        try {
            translateCriteria(criteres);
            List<PropositionNonPromouvableAGDTO> list = ucVerificationPropositionAGCAP.findListPropositionNonPromouvableAGDTO(tri, criteres, firstLine, limitLine, filter);
            return mapper.cloneToGwt(list, PropositionNonPromouvableAGDTOGWT.class);
        } catch (BusinessException e) {
            if (SeditException.Type.MULTIPLE_EXCEPTION.equals(e.getType()))
                throw new SeditGwtException(e.getExceptionMap());
            else throw new SeditGwtException(SeditGwtException.TYPE_BUSINESS, e);
        } catch (TechnicalException e) {
            if (SeditException.Type.MULTIPLE_EXCEPTION.equals(e.getType()))
                throw new SeditGwtException(e.getExceptionMap());
            else throw new SeditGwtException(SeditGwtException.TYPE_TECHNICAL, e);
        } catch (Exception e) {
            throw new SeditGwtException(SeditGwtException.TYPE_INCONNUE, e);
        }
    }

    /**
     * @throws SeditGwtException
     * @see {@link IGwtServiceCaAvg#countAgentOfTableauAG(Map, String))}
     */
    @Override
    public Long countAgentOfTableauAG(Map<String, Object> criteria, String filter) throws SeditGwtException {
        try {
            TableauAGGWT tableauAG = (TableauAGGWT) criteria.get("tableauAG");
            if (tableauAG != null) {
                criteria.put("tableauAG", mapper.cloneToModel(tableauAG, TableauAG.class));
            }
            return ucAgent4Grade.countAgentOfTableauAG(criteria, filter);
        } catch (BusinessException e) {
            if (SeditException.Type.MULTIPLE_EXCEPTION.equals(e.getType()))
                throw new SeditGwtException(e.getExceptionMap());
            else throw new SeditGwtException(SeditGwtException.TYPE_BUSINESS, e);
        } catch (TechnicalException e) {
            if (SeditException.Type.MULTIPLE_EXCEPTION.equals(e.getType()))
                throw new SeditGwtException(e.getExceptionMap());
            else throw new SeditGwtException(SeditGwtException.TYPE_TECHNICAL, e);
        } catch (Exception e) {
            throw new SeditGwtException(SeditGwtException.TYPE_INCONNUE, e);
        }
    }

    /**
     * @throws SeditGwtException
     * @see {@link IGwtServiceCaAvg#findListAgentAyantPropositionDansTableauAG(Map, Map, long, long, String))}
     */
    @Override
    public List<List<Serializable>> findListAgentAyantPropositionDansTableauAG(Map<String, Integer> tri, Map<String, Object> criteres, long firstLine, long limitLine, String filter) throws SeditGwtException {
        try {
            TableauAGGWT tableauAG = (TableauAGGWT) criteres.get("tableauAG");
            if (tableauAG != null) {
                criteres.put("tableauAG", mapper.cloneToModel(tableauAG, TableauAG.class));
            }
            List<AgentDTO> listAgentDto = ucAgent4Grade.findListAgentAyantPropositionDansTableauAG(tri, criteres, firstLine, limitLine, filter);
            List<List<Serializable>> listRowsGWT = new ArrayList<List<Serializable>>();
            List<Serializable> listColsGWT = null;
            AgentDTOGWT agentGWT = null;
            for (AgentDTO agent : listAgentDto) {
                agentGWT = mapper.cloneToGwt(agent, AgentDTOGWT.class);
                listColsGWT = new ArrayList<Serializable>();
                listColsGWT.add(agentGWT.getKey());
                listColsGWT.add(agentGWT.getNom());
                listColsGWT.add(agentGWT.getPrenom());
                listColsGWT.add(agentGWT.getMatricule());
                listColsGWT.add(agentGWT.getStatutLibelle());
                listColsGWT.add(agentGWT.getServiceLibelle());
                listColsGWT.add(agentGWT.getCollectiviteLibelle());

                // Ajout des colonnes à la ligne
                listRowsGWT.add(listColsGWT);
            }

            return listRowsGWT;
        } catch (BusinessException e) {
            if (SeditException.Type.MULTIPLE_EXCEPTION.equals(e.getType()))
                throw new SeditGwtException(e.getExceptionMap());
            else throw new SeditGwtException(SeditGwtException.TYPE_BUSINESS, e);
        } catch (TechnicalException e) {
            if (SeditException.Type.MULTIPLE_EXCEPTION.equals(e.getType()))
                throw new SeditGwtException(e.getExceptionMap());
            else throw new SeditGwtException(SeditGwtException.TYPE_TECHNICAL, e);
        } catch (Exception e) {
            throw new SeditGwtException(SeditGwtException.TYPE_INCONNUE, e);
        }
    }

    /**
     * @throws SeditGwtException
     * @see {@link IGwtServiceCaAvg#findListAgentDansTableauAGByNameOrMatricule(Map))}
     */
    @Override
    public List<AgentDTOGWT> findListAgentDansTableauAGByNameOrMatricule(Map<String, Object> criteres) throws SeditGwtException {
        try {
            TableauAGGWT tableauAG = (TableauAGGWT) criteres.get("tableauAG");
            if (tableauAG != null) {
                criteres.put("tableauAG", mapper.cloneToModel(tableauAG, TableauAG.class));
            }
            List<AgentDTO> listAgentDto = ucAgent4Grade.findListAgentAyantPropositionDansTableauAG(null, criteres, -1, 10, null);
            List<AgentDTOGWT> listAgentDtoGwt = mapper.cloneToGwt(listAgentDto, AgentDTOGWT.class);
            return listAgentDtoGwt;
        } catch (BusinessException e) {
            if (SeditException.Type.MULTIPLE_EXCEPTION.equals(e.getType()))
                throw new SeditGwtException(e.getExceptionMap());
            else throw new SeditGwtException(SeditGwtException.TYPE_BUSINESS, e);
        } catch (TechnicalException e) {
            if (SeditException.Type.MULTIPLE_EXCEPTION.equals(e.getType()))
                throw new SeditGwtException(e.getExceptionMap());
            else throw new SeditGwtException(SeditGwtException.TYPE_TECHNICAL, e);
        } catch (Exception e) {
            throw new SeditGwtException(SeditGwtException.TYPE_INCONNUE, e);
        }
    }

    /**
     * @throws SeditGwtException
     * @see {@link IGwtServiceCaAvg#findListAgentDTOByNomOrMatricule(String, OrganismeGWT))}
     */
    @Override
    public List<AgentDTOGWT> findListAgentDTOByNomOrMatricule(String nomOuMatricule, OrganismeGWT organisme) throws SeditGwtException {
        try {
            // FIXME mal utilisé (voir QuickSearchAgentTableauAG), à remplacer par un vrai QS agent...
            Map<String, Serializable> criteres = new HashMap<String, Serializable>();
            criteres.put("nomOuMatricule", nomOuMatricule);
            criteres.put("organisme", organisme.getId());
            criteres.put("onlyAgentPresent", true);

            List<AgentDTO> listAgentDto = ucAgent4Grade.findListAgentDTOByNomOrMatricule(criteres);
            List<AgentDTOGWT> listAgentDtoGwt = mapper.cloneToGwt(listAgentDto, AgentDTOGWT.class);
            return listAgentDtoGwt;
        } catch (BusinessException e) {
            if (SeditException.Type.MULTIPLE_EXCEPTION.equals(e.getType()))
                throw new SeditGwtException(e.getExceptionMap());
            else throw new SeditGwtException(SeditGwtException.TYPE_BUSINESS, e);
        } catch (TechnicalException e) {
            if (SeditException.Type.MULTIPLE_EXCEPTION.equals(e.getType()))
                throw new SeditGwtException(e.getExceptionMap());
            else throw new SeditGwtException(SeditGwtException.TYPE_TECHNICAL, e);
        } catch (Exception e) {
            throw new SeditGwtException(SeditGwtException.TYPE_INCONNUE, e);
        }
    }

    /**
     * @throws SeditGwtException
     * @see {@link IGwtServiceCaAvg#exportPropositionAGForExcel(Map, Map, String)
     */
    @Override
    public String exportPropositionAGForExcel(Map<String, Integer> tri, Map<String, Object> criteres, String filter) throws SeditGwtException {
        try {
            if (criteres.containsKey("tableauAG")) {
            	Object objTableauAGId = criteres.get("tableauAG");
            	Integer tableauAGId;
            	if ( objTableauAGId instanceof Integer) {
            		tableauAGId = (Integer) criteres.get("tableauAG");
            	}else {
            		tableauAGId = Integer.valueOf((String) criteres.get("tableauAG"));
            	}

                if (tableauAGId != null) {
                    criteres.put("tableauAGId", tableauAGId);
                }
            }

            if (criteres.containsKey("promouvable")) {
                Object objPromouvable = criteres.get("promouvable");
                Boolean promouvable = Boolean.FALSE;
                if (objPromouvable instanceof Boolean) {
                    promouvable = (Boolean) objPromouvable;
                } else {
                    promouvable = Boolean.valueOf((String) objPromouvable);
                }
                criteres.put("promouvable", promouvable);
            } else if (criteres.containsKey("promu")) {
                Object objPromu = criteres.get("promu");
                Boolean promu = Boolean.FALSE;
                if (objPromu instanceof Boolean) {
                    promu = (Boolean) objPromu;
                } else {
                    promu = Boolean.valueOf((String) objPromu);
                }
                criteres.put("promu", promu);
            }

            if (criteres.containsKey("nonPromouvable")) {
            	Object objNonPromouv = criteres.containsKey("nonPromouvable");
            	Boolean nonPromouvable = false;
            	if ( objNonPromouv instanceof Boolean) {
            		nonPromouvable = (Boolean) criteres.get("nonPromouvable");
            	}else {
            		nonPromouvable = Boolean.valueOf((String) criteres.get("nonPromouvable"));
            	}
                criteres.put("nonPromouvable", nonPromouvable);
            }
            if (criteres.containsKey("promuExport")) {
            	Object objPromuExport= criteres.containsKey("promuExport");
            	Boolean promuExport = false;
            	if ( objPromuExport instanceof Boolean) {
            		promuExport = (Boolean) criteres.get("promuExport");
            	}else {
            		promuExport = Boolean.valueOf((String) criteres.get("promuExport"));
            	}
                criteres.put("promuExport", promuExport);
            }
            if (criteres.containsKey("withConcoursAgent")) {
            	Object objWithConcoursAgent= criteres.containsKey("withConcoursAgent");
            	Boolean withConcoursAgent = false;
            	if ( objWithConcoursAgent instanceof Boolean) {
            		withConcoursAgent = (Boolean) criteres.get("withConcoursAgent");
            	}else {
            		withConcoursAgent = Boolean.valueOf((String) criteres.get("withConcoursAgent"));
            	}
                criteres.put("withConcoursAgent", withConcoursAgent);
            }
            if (criteres.containsKey("withConcours")) {
            	Object objWithConcours = criteres.containsKey("withConcours");
            	Boolean withConcours = false;
            	if ( objWithConcours instanceof Boolean) {
            		withConcours = (Boolean) criteres.get("withConcours");
            	}else {
            		withConcours = Boolean.valueOf((String) criteres.get("withConcours"));
            	}
                criteres.put("withConcours", withConcours);
            }
            
            
            return ucVerificationPropositionAGCAP.exportPropositionAGForExcel(criteres);
        } catch (BusinessException e) {
            if (SeditException.Type.MULTIPLE_EXCEPTION.equals(e.getType()))
                throw new SeditGwtException(e.getExceptionMap());
            else throw new SeditGwtException(SeditGwtException.TYPE_BUSINESS, e);
        } catch (TechnicalException e) {
            if (SeditException.Type.MULTIPLE_EXCEPTION.equals(e.getType()))
                throw new SeditGwtException(e.getExceptionMap());
            else throw new SeditGwtException(SeditGwtException.TYPE_TECHNICAL, e);
        } catch (Exception e) {
            throw new SeditGwtException(SeditGwtException.TYPE_INCONNUE, e);
        }
    }

    /**
     * @throws SeditGwtException
     * @see {@link IGwtServiceCaAvg#calculListDetailAvancementVerificationPropositionAg(Integer)
     */
    @Override
    public List<List<Serializable>> calculListDetailAvancementVerificationPropositionAg(Integer tableauId) throws SeditGwtException {
        try {
            List<DetailAvancementAGDTO> listDetail = ucVerificationPropositionAGCAP.calculListDetailAvancementVerificationPropositionAg(tableauId);
            List<List<Serializable>> listRows = new ArrayList<List<Serializable>>();
            for (DetailAvancementAGDTO detail : listDetail) {
                List<Serializable> theRow = new ArrayList<Serializable>();
                theRow.add(new Key(detail.getId(), detail.getVersion()));
                theRow.add(detail.getGradeLibelle());
                theRow.add(Integer.toString(detail.getNbPromouvableTotal()));
                theRow.add(Integer.toString(detail.getNbPromouvableAVerifiee()));
                theRow.add(Integer.toString(detail.getNbPromouvableVerifiee()));
                theRow.add(Integer.toString(detail.getPourcentagePromouvableVerifiee()));
                theRow.add(Integer.toString(detail.getNbNonPromouvableTotal()));
                theRow.add(Integer.toString(detail.getNbNonPromouvableAVerifiee()));
                theRow.add(Integer.toString(detail.getNbNonPromouvableVerifiee()));
                theRow.add(Integer.toString(detail.getPourcentageNonPromouvableVerifiee()));

                listRows.add(theRow);
            }
            return listRows;

        } catch (BusinessException e) {
            if (SeditException.Type.MULTIPLE_EXCEPTION.equals(e.getType()))
                throw new SeditGwtException(e.getExceptionMap());
            else throw new SeditGwtException(SeditGwtException.TYPE_BUSINESS, e);
        } catch (TechnicalException e) {
            if (SeditException.Type.MULTIPLE_EXCEPTION.equals(e.getType()))
                throw new SeditGwtException(e.getExceptionMap());
            else throw new SeditGwtException(SeditGwtException.TYPE_TECHNICAL, e);
        } catch (Exception e) {
            throw new SeditGwtException(SeditGwtException.TYPE_INCONNUE, e);
        }
    }

    /**
     * @throws SeditGwtException
     * @see {@link IGwtServiceCaAvg#changeDateOfPropositionAG(Integer, String, Date)}
     */
    @Override
    public void changeDateOfPropositionAG(Integer propositionId, String nomChamps, Date date) throws SeditGwtException {
        try {
            ucElaborationCAP.changeDateOfPropositionAG(propositionId, nomChamps, date);
        } catch (BusinessException e) {
            if (SeditException.Type.MULTIPLE_EXCEPTION.equals(e.getType()))
                throw new SeditGwtException(e.getExceptionMap());
            else throw new SeditGwtException(SeditGwtException.TYPE_BUSINESS, e);
        } catch (TechnicalException e) {
            if (SeditException.Type.MULTIPLE_EXCEPTION.equals(e.getType()))
                throw new SeditGwtException(e.getExceptionMap());
            else throw new SeditGwtException(SeditGwtException.TYPE_TECHNICAL, e);
        } catch (Exception e) {
            throw new SeditGwtException(SeditGwtException.TYPE_INCONNUE, e);
        }
    }

    /**
     * @throws SeditGwtException
     * @see {@link IGwtServiceCaAvg#changePromuPropositionAG(Integer, boolean)}
     */
    @Override
    public void changePromuPropositionAG(Integer propositionId, boolean promu) throws SeditGwtException {
        try {
            ucElaborationCAP.changePromuPropositionAG(propositionId, promu);
        } catch (BusinessException e) {
            if (SeditException.Type.MULTIPLE_EXCEPTION.equals(e.getType()))
                throw new SeditGwtException(e.getExceptionMap());
            else throw new SeditGwtException(SeditGwtException.TYPE_BUSINESS, e);
        } catch (TechnicalException e) {
            if (SeditException.Type.MULTIPLE_EXCEPTION.equals(e.getType()))
                throw new SeditGwtException(e.getExceptionMap());
            else throw new SeditGwtException(SeditGwtException.TYPE_TECHNICAL, e);
        } catch (Exception e) {
            throw new SeditGwtException(SeditGwtException.TYPE_INCONNUE, e);
        }
    }

    /**
     * @throws SeditGwtException
     * @see {@link IGwtServiceCaAvg#findPropositonAGById(Integer)
     */
    @Override
    public PropositionAGGWT findPropositonAGById(Integer propositionId) throws SeditGwtException {
        try {
            PropositionAG serverResult = ucPropositionAG.findPropositonAGById(propositionId);
            if (serverResult == null) return null;
            PropositionAGGWT result = mapper.cloneToGwt(serverResult, PropositionAGGWT.class);

            return result;
        } catch (BusinessException e) {
            if (SeditException.Type.MULTIPLE_EXCEPTION.equals(e.getType()))
                throw new SeditGwtException(e.getExceptionMap());
            else throw new SeditGwtException(SeditGwtException.TYPE_BUSINESS, e);
        } catch (TechnicalException e) {
            if (SeditException.Type.MULTIPLE_EXCEPTION.equals(e.getType()))
                throw new SeditGwtException(e.getExceptionMap());
            else throw new SeditGwtException(SeditGwtException.TYPE_TECHNICAL, e);
        } catch (Exception e) {
            throw new SeditGwtException(SeditGwtException.TYPE_INCONNUE, e);
        }
    }

    /**
     * @throws SeditGwtException
     * @see {@link IGwtServiceCaAvg#loadCompletePropositonAGById(Integer)
     */
    @Override
    public PropositionAGGWT loadCompletePropositonAGById(Integer propositionId) throws SeditGwtException {
        try {

            PropositionAG serverResult = ucPropositionAG.loadCompletePropositonAGById(propositionId);
            PropositionAGGWT result = mapper.cloneToGwt(serverResult, PropositionAGGWT.class);
            return result;

        } catch (BusinessException e) {
            if (SeditException.Type.MULTIPLE_EXCEPTION.equals(e.getType()))
                throw new SeditGwtException(e.getExceptionMap());
            else throw new SeditGwtException(SeditGwtException.TYPE_BUSINESS, e);
        } catch (TechnicalException e) {
            if (SeditException.Type.MULTIPLE_EXCEPTION.equals(e.getType()))
                throw new SeditGwtException(e.getExceptionMap());
            else throw new SeditGwtException(SeditGwtException.TYPE_TECHNICAL, e);
        } catch (Exception e) {
            throw new SeditGwtException(SeditGwtException.TYPE_INCONNUE, e);
        }
    }

    /**
     * @throws SeditGwtException
     * @see {@link IGwtServiceCaAvg#findListConditionAG(Integer)
     */
    @Override
    public List<ConditionDePropositionDTOGWT> findListConditionAG(Integer propositionId) throws SeditGwtException {
        try {
            List<ConditionDePropositionDTO> serverList = ucPropositionAG.findListConditionAG(propositionId);
            if (serverList == null) return new ArrayList<ConditionDePropositionDTOGWT>();

            return mapper.cloneToGwt(serverList, ConditionDePropositionDTOGWT.class);
        } catch (BusinessException e) {
            if (SeditException.Type.MULTIPLE_EXCEPTION.equals(e.getType()))
                throw new SeditGwtException(e.getExceptionMap());
            else throw new SeditGwtException(SeditGwtException.TYPE_BUSINESS, e);
        } catch (TechnicalException e) {
            if (SeditException.Type.MULTIPLE_EXCEPTION.equals(e.getType()))
                throw new SeditGwtException(e.getExceptionMap());
            else throw new SeditGwtException(SeditGwtException.TYPE_TECHNICAL, e);
        } catch (Exception e) {
            throw new SeditGwtException(SeditGwtException.TYPE_INCONNUE, e);
        }
    }

    /**
     * @throws SeditGwtException
     * @see {@link IGwtServiceCaAvg#countPropositionAGInjection(Map, String)}
     */
    @Override
    public Long countPropositionAGInjection(Map<String, Object> criteria, String filter) throws SeditGwtException {
        try {
            translateCriteria(criteria);
            return ucFinalisationCAP.countPropositionAGInjection(criteria, filter);
        } catch (BusinessException e) {
            if (SeditException.Type.MULTIPLE_EXCEPTION.equals(e.getType()))
                throw new SeditGwtException(e.getExceptionMap());
            else throw new SeditGwtException(SeditGwtException.TYPE_BUSINESS, e);
        } catch (TechnicalException e) {
            if (SeditException.Type.MULTIPLE_EXCEPTION.equals(e.getType()))
                throw new SeditGwtException(e.getExceptionMap());
            else throw new SeditGwtException(SeditGwtException.TYPE_TECHNICAL, e);
        } catch (Exception e) {
            throw new SeditGwtException(SeditGwtException.TYPE_INCONNUE, e);
        }
    }

    /**
     * @throws SeditGwtException
     * @see {@link IGwtServiceCaAvg#findPropositionAGInjection(Map, Map, long, long, String)}
     */
    @Override
    public List<PropositionAGDTOGWT> findPropositionAGInjection(Map<String, Integer> tri, Map<String, Object> criteres, long firstLine, long limitLine, String filter) throws SeditGwtException {
        try {
            translateCriteria(criteres);
            List<PropositionAGDTO> list = ucFinalisationCAP.findPropositionAGInjection(tri, criteres, firstLine, limitLine, filter);
            return mapper.cloneToGwt(list, PropositionAGDTOGWT.class);
        } catch (BusinessException e) {
            if (SeditException.Type.MULTIPLE_EXCEPTION.equals(e.getType()))
                throw new SeditGwtException(e.getExceptionMap());
            else throw new SeditGwtException(SeditGwtException.TYPE_BUSINESS, e);
        } catch (TechnicalException e) {
            if (SeditException.Type.MULTIPLE_EXCEPTION.equals(e.getType()))
                throw new SeditGwtException(e.getExceptionMap());
            else throw new SeditGwtException(SeditGwtException.TYPE_TECHNICAL, e);
        } catch (Exception e) {
            throw new SeditGwtException(SeditGwtException.TYPE_INCONNUE, e);
        }
    }

    /**
     * @see fr.bl.client.grh.ca.avg.remote.IGwtServiceCaAvg#searchAvisAG(java.util.HashMap, java.util.HashMap, long, long, java.lang.String)
     */
    @Override
    public List<AvisAGDTOGWT> searchAvisAG(final HashMap<String, Integer> tris, final HashMap<String, Object> criteres, final long numLigne, final long nbLignes, final String filtre) throws SeditGwtException {
        try {
            List<AvisAGDTO> serverResult = ucAvisAG.findListAvisAG(tris, criteres, numLigne, nbLignes, filtre);
            return mapper.cloneToGwt(serverResult, AvisAGDTOGWT.class);

        } catch (BusinessException e) {
            if (SeditException.Type.MULTIPLE_EXCEPTION.equals(e.getType()))
                throw new SeditGwtException(e.getExceptionMap());
            else throw new SeditGwtException(SeditGwtException.TYPE_BUSINESS, e);
        } catch (TechnicalException e) {
            if (SeditException.Type.MULTIPLE_EXCEPTION.equals(e.getType()))
                throw new SeditGwtException(e.getExceptionMap());
            else throw new SeditGwtException(SeditGwtException.TYPE_TECHNICAL, e);
        } catch (Exception e) {
            throw new SeditGwtException(SeditGwtException.TYPE_INCONNUE, e);
        }
    }

    /**
     * Retourne le nombre d'avis AG
     *
     * @param criteres
     * @param filtre
     * @throws SeditGwtException
     */
    @Override
    public Long countAllAvisAG(final HashMap<String, Object> criteres, final String filtre) throws SeditGwtException {
        try {
            return ucAvisAG.countAvisAG(criteres, filtre);
        } catch (BusinessException e) {
            if (SeditException.Type.MULTIPLE_EXCEPTION.equals(e.getType()))
                throw new SeditGwtException(e.getExceptionMap());
            else throw new SeditGwtException(SeditGwtException.TYPE_BUSINESS, e);
        } catch (TechnicalException e) {
            if (SeditException.Type.MULTIPLE_EXCEPTION.equals(e.getType()))
                throw new SeditGwtException(e.getExceptionMap());
            else throw new SeditGwtException(SeditGwtException.TYPE_TECHNICAL, e);
        } catch (Exception e) {
            throw new SeditGwtException(SeditGwtException.TYPE_INCONNUE, e);
        }
    }

    /**
     * Retourne la liste des justificatifs de retard par detailCondition
     *
     * @param propositionId
     * @return
     */
    @Override
    public List<JustificatifRetardAGDTOGWT> findListJustificatifsRetardAG(Integer detailId) throws SeditGwtException {
        try {
            List<JustificatifRetardAGDTO> serverResult = ucPropositionAG.findJustificatifAGByDetailConditionId(detailId);
            return mapper.cloneToGwt(serverResult, JustificatifRetardAGDTOGWT.class);

        } catch (BusinessException e) {
            if (SeditException.Type.MULTIPLE_EXCEPTION.equals(e.getType()))
                throw new SeditGwtException(e.getExceptionMap());
            else throw new SeditGwtException(SeditGwtException.TYPE_BUSINESS, e);
        } catch (TechnicalException e) {
            if (SeditException.Type.MULTIPLE_EXCEPTION.equals(e.getType()))
                throw new SeditGwtException(e.getExceptionMap());
            else throw new SeditGwtException(SeditGwtException.TYPE_TECHNICAL, e);
        } catch (Exception e) {
            throw new SeditGwtException(SeditGwtException.TYPE_INCONNUE, e);
        }
    }

    /**
     * Retourne la liste des échelons du grade et leurs indices bruts et majorés
     *
     * @param codeGrade
     * @param cadreStatutaire
     * @param dateDebut
     * @param dateFin
     * @return List<EchelonEtIndicesDTOGWT>
     */
    @Override
    public List<EchelonEtIndicesDTOGWT> findListEchelonAndIndiceOfGradeByCode(String codeGrade, CadreStatutaireGWT cadreStatutaire, Date dateDebut, Date dateFin) throws SeditGwtException {
        try {
            List<EchelonEtIndicesDTO> serverResult = ucGrade.findListEchelonAndIndiceOfGradeByCode(codeGrade, mapper.cloneToModel(cadreStatutaire, CadreStatutaire.class), dateDebut, dateFin);
            return mapper.cloneToGwt(serverResult, EchelonEtIndicesDTOGWT.class);
        } catch (BusinessException e) {
            if (SeditException.Type.MULTIPLE_EXCEPTION.equals(e.getType()))
                throw new SeditGwtException(e.getExceptionMap());
            else throw new SeditGwtException(SeditGwtException.TYPE_BUSINESS, e);
        } catch (TechnicalException e) {
            if (SeditException.Type.MULTIPLE_EXCEPTION.equals(e.getType()))
                throw new SeditGwtException(e.getExceptionMap());
            else throw new SeditGwtException(SeditGwtException.TYPE_TECHNICAL, e);
        } catch (Exception e) {
            throw new SeditGwtException(SeditGwtException.TYPE_INCONNUE, e);
        }
    }

    /**
     * Enregistre la proposition
     *
     * @param propositionAG
     * @param map<String,Object>
     */
    @Override
    public void savePropositionAG(PropositionAGGWT propositionAGGWT, HashMap<String, Object> map) throws SeditGwtException {
        try {
            PropositionAG propositionAG = mapper.cloneToModel(propositionAGGWT, PropositionAG.class);
            this.ucPropositionAG.savePropositionAGForVisuModifProposition(propositionAG, map);
        } catch (BusinessException e) {
            if (SeditException.Type.MULTIPLE_EXCEPTION.equals(e.getType()))
                throw new SeditGwtException(e.getExceptionMap());
            else throw new SeditGwtException(SeditGwtException.TYPE_BUSINESS, e);
        } catch (TechnicalException e) {
            if (SeditException.Type.MULTIPLE_EXCEPTION.equals(e.getType()))
                throw new SeditGwtException(e.getExceptionMap());
            else throw new SeditGwtException(SeditGwtException.TYPE_TECHNICAL, e);
        } catch (Exception e) {
            throw new SeditGwtException(SeditGwtException.TYPE_INCONNUE, e);
        }
    }

    /**
     * Enregistre le rang classement de la proposition
     *
     * @param rangClassementAG
     */
    @Override
    public void saveRangClassementAG(RangClassementAGGWT rangClassementAGGWT) throws SeditGwtException {
        try {
            ucPropositionAG.saveRangClassementAG(mapper.cloneToModel(rangClassementAGGWT, RangClassementAG.class));
        } catch (BusinessException e) {
            if (SeditException.Type.MULTIPLE_EXCEPTION.equals(e.getType()))
                throw new SeditGwtException(e.getExceptionMap());
            else throw new SeditGwtException(SeditGwtException.TYPE_BUSINESS, e);
        } catch (TechnicalException e) {
            if (SeditException.Type.MULTIPLE_EXCEPTION.equals(e.getType()))
                throw new SeditGwtException(e.getExceptionMap());
            else throw new SeditGwtException(SeditGwtException.TYPE_TECHNICAL, e);
        } catch (Exception e) {
            throw new SeditGwtException(SeditGwtException.TYPE_INCONNUE, e);
        }
    }

    /**
     * Initialise la proposition à partir de la carriere et retourne la proposition enregistrée
     *
     * @param carriereId
     * @param codeRegroupement
     * @return PropositionAGGWT
     */
    @Override
    public PropositionAGGWT initPropositionAndReturnPropositionAG(Integer tableauAGId, String carriereId, Long codeRegroupement) throws SeditGwtException {
        try {
            PropositionAG propositionAG = ucPropositionAG.initPropositionAndReturnPropositionAG(tableauAGId, carriereId, codeRegroupement);
            return mapper.cloneToGwt(propositionAG, PropositionAGGWT.class);
        } catch (BusinessException e) {
            if (SeditException.Type.MULTIPLE_EXCEPTION.equals(e.getType()))
                throw new SeditGwtException(e.getExceptionMap());
            else throw new SeditGwtException(SeditGwtException.TYPE_BUSINESS, e);
        } catch (TechnicalException e) {
            if (SeditException.Type.MULTIPLE_EXCEPTION.equals(e.getType()))
                throw new SeditGwtException(e.getExceptionMap());
            else throw new SeditGwtException(SeditGwtException.TYPE_TECHNICAL, e);
        } catch (Exception e) {
            throw new SeditGwtException(SeditGwtException.TYPE_INCONNUE, e);
        }
    }

    /**
     * @see IGwtServiceCaAvg#verifieIntegriteProposition(Integer, String, Long, String)
     */
    @Override
    public PropositionAGGWT verifieIntegriteProposition(Integer tableauAGId, String carriereId, Long codeRegroupement, String gradeCibleId) throws SeditGwtException {
        try {
            PropositionAG propositionAG = ucPropositionAG.loadDoublonProposition(tableauAGId, carriereId, codeRegroupement, gradeCibleId);
            if (propositionAG != null) {
                return mapper.cloneToGwt(propositionAG, PropositionAGGWT.class);
            } else {
                return null;
            }
        } catch (BusinessException e) {
            if (SeditException.Type.MULTIPLE_EXCEPTION.equals(e.getType()))
                throw new SeditGwtException(e.getExceptionMap());
            else throw new SeditGwtException(SeditGwtException.TYPE_BUSINESS, e);
        } catch (TechnicalException e) {
            if (SeditException.Type.MULTIPLE_EXCEPTION.equals(e.getType()))
                throw new SeditGwtException(e.getExceptionMap());
            else throw new SeditGwtException(SeditGwtException.TYPE_TECHNICAL, e);
        } catch (Exception e) {
            throw new SeditGwtException(SeditGwtException.TYPE_INCONNUE, e);
        }
    }

    /**
     * Supprime l'avis AG
     *
     * @param avisAG
     */
    @Override
    public void deleteAvisAG(AvisAGGWT avisAG) throws SeditGwtException {
        try {
            ucAvisAG.deleteById(avisAG.getId());
        } catch (BusinessException e) {
            if (SeditException.Type.MULTIPLE_EXCEPTION.equals(e.getType()))
                throw new SeditGwtException(e.getExceptionMap());
            else throw new SeditGwtException(SeditGwtException.TYPE_BUSINESS, e);
        } catch (TechnicalException e) {
            if (SeditException.Type.MULTIPLE_EXCEPTION.equals(e.getType()))
                throw new SeditGwtException(e.getExceptionMap());
            else throw new SeditGwtException(SeditGwtException.TYPE_TECHNICAL, e);
        } catch (Exception e) {
            throw new SeditGwtException(SeditGwtException.TYPE_INCONNUE, e);
        }
    }

    /**
     * Recherche l'avis par son id
     *
     * @param avisAGId
     */
    @Override
    public AvisAGGWT findAvisAGById(Integer avisAGId) throws SeditGwtException {
        try {
            AvisAG serverResult = ucAvisAG.findWithAgentAndAppreciationById(avisAGId);
            return mapper.cloneToGwt(serverResult, AvisAGGWT.class);
        } catch (BusinessException e) {
            if (SeditException.Type.MULTIPLE_EXCEPTION.equals(e.getType()))
                throw new SeditGwtException(e.getExceptionMap());
            else throw new SeditGwtException(SeditGwtException.TYPE_BUSINESS, e);
        } catch (TechnicalException e) {
            if (SeditException.Type.MULTIPLE_EXCEPTION.equals(e.getType()))
                throw new SeditGwtException(e.getExceptionMap());
            else throw new SeditGwtException(SeditGwtException.TYPE_TECHNICAL, e);
        } catch (Exception e) {
            throw new SeditGwtException(SeditGwtException.TYPE_INCONNUE, e);
        }
    }

    /**
     * Update l'avis
     *
     * @param avisAG
     * @param propositionId
     */
    @Override
    public AvisAGGWT updateAvisAG(AvisAGGWT avisAG, Integer propositionId) throws SeditGwtException {
        try {
            AvisAG serverParam = mapper.cloneToModel(avisAG, AvisAG.class);
            AvisAG serverResult = ucAvisAG.update(serverParam, propositionId);
            return mapper.cloneToGwt(serverResult, AvisAGGWT.class);
        } catch (BusinessException e) {
            if (SeditException.Type.MULTIPLE_EXCEPTION.equals(e.getType()))
                throw new SeditGwtException(e.getExceptionMap());
            else throw new SeditGwtException(SeditGwtException.TYPE_BUSINESS, e);
        } catch (TechnicalException e) {
            if (SeditException.Type.MULTIPLE_EXCEPTION.equals(e.getType()))
                throw new SeditGwtException(e.getExceptionMap());
            else throw new SeditGwtException(SeditGwtException.TYPE_TECHNICAL, e);
        } catch (Exception e) {
            throw new SeditGwtException(SeditGwtException.TYPE_INCONNUE, e);
        }
    }

    /**
     * Supprime l'avis AG
     *
     * @param avisId
     */
    @Override
    public void deleteAvisAGById(Integer avisId) throws SeditGwtException {
        try {
            ucAvisAG.deleteById(avisId);
        } catch (BusinessException e) {
            if (SeditException.Type.MULTIPLE_EXCEPTION.equals(e.getType()))
                throw new SeditGwtException(e.getExceptionMap());
            else throw new SeditGwtException(SeditGwtException.TYPE_BUSINESS, e);
        } catch (TechnicalException e) {
            if (SeditException.Type.MULTIPLE_EXCEPTION.equals(e.getType()))
                throw new SeditGwtException(e.getExceptionMap());
            else throw new SeditGwtException(SeditGwtException.TYPE_TECHNICAL, e);
        } catch (Exception e) {
            throw new SeditGwtException(SeditGwtException.TYPE_INCONNUE, e);
        }
    }

    /**
     * Retourne la liste des appreciation par l'organisme return List<ParamAppreciationAvisDTOGWT>
     */
    @Override
    public List<ParamAppreciationAvisDTOGWT> findListAppreciation(String organismeId, EnumTypeAEAGGWT typeGWT) throws SeditGwtException {
        try {
            EnumTypeAEAG type = mapper.cloneToModel(typeGWT, EnumTypeAEAG.class);
            List<ParamAppreciationAvisDTO> serverResult = ucAvisAG.findListParamAppreciationAvis(organismeId, type);
            return mapper.cloneToGwt(serverResult, ParamAppreciationAvisDTOGWT.class);
        } catch (BusinessException e) {
            if (SeditException.Type.MULTIPLE_EXCEPTION.equals(e.getType()))
                throw new SeditGwtException(e.getExceptionMap());
            else throw new SeditGwtException(SeditGwtException.TYPE_BUSINESS, e);
        } catch (TechnicalException e) {
            if (SeditException.Type.MULTIPLE_EXCEPTION.equals(e.getType()))
                throw new SeditGwtException(e.getExceptionMap());
            else throw new SeditGwtException(SeditGwtException.TYPE_TECHNICAL, e);
        } catch (Exception e) {
            throw new SeditGwtException(SeditGwtException.TYPE_INCONNUE, e);
        }
    }

    /**
     * Update l'avis AG
     *
     * @param avisId
     * @param appreciationId
     * @param qualite
     * @param commentaire
     */
    @Override
    public AvisAGGWT updateAvisAG(UtilisateurSMRHGWT utilisateurGWT, Integer avisId, Integer propositionId, Integer appreciationId, String qualite, String commentaire) throws SeditGwtException {
        try {
            UtilisateurSMRH utilisateur = mapper.cloneToModel(utilisateurGWT, UtilisateurSMRH.class);
            AvisAG result = ucAvisAG.updateAvisAG(utilisateur, avisId, propositionId, appreciationId, qualite, commentaire);
            return mapper.cloneToGwt(result, AvisAGGWT.class);
        } catch (BusinessException e) {
            if (SeditException.Type.MULTIPLE_EXCEPTION.equals(e.getType()))
                throw new SeditGwtException(e.getExceptionMap());
            else throw new SeditGwtException(SeditGwtException.TYPE_BUSINESS, e);
        } catch (TechnicalException e) {
            if (SeditException.Type.MULTIPLE_EXCEPTION.equals(e.getType()))
                throw new SeditGwtException(e.getExceptionMap());
            else throw new SeditGwtException(SeditGwtException.TYPE_TECHNICAL, e);
        } catch (Exception e) {
            throw new SeditGwtException(SeditGwtException.TYPE_INCONNUE, e);
        }
    }

    @Override
    public void updateAllAvisAG4CurrentAgentAndDate(UtilisateurSMRHGWT utilisateurGWT, List<AvisAGDTOGWT> listAvisDto) throws SeditGwtException {
        try {
            AvisAGDTOGWT avisGWT = null;
            AvisAGDTO avis = null;
            List<AvisAGDTO> listavis = new ArrayList<AvisAGDTO>();
            for (int i = 0; i < listAvisDto.size(); i++) {
                avisGWT = listAvisDto.get(i);
                avis = mapper.cloneToModel(avisGWT, AvisAGDTO.class);
                listavis.add(avis);
            }
            //List listavis = (List)mapper.cloneToModel(listAvisDto, AvisAGDTO.class);
            UtilisateurSMRH utilisateur = mapper.cloneToModel(utilisateurGWT, UtilisateurSMRH.class);
            ucAvisAG.updateAllAvisAG4CurrentAgentAndDate(utilisateur, listavis);
        } catch (BusinessException e) {
            if (SeditException.Type.MULTIPLE_EXCEPTION.equals(e.getType()))
                throw new SeditGwtException(e.getExceptionMap());
            else throw new SeditGwtException(SeditGwtException.TYPE_BUSINESS, e);
        } catch (TechnicalException e) {
            if (SeditException.Type.MULTIPLE_EXCEPTION.equals(e.getType()))
                throw new SeditGwtException(e.getExceptionMap());
            else throw new SeditGwtException(SeditGwtException.TYPE_TECHNICAL, e);
        } catch (Exception e) {
            throw new SeditGwtException(SeditGwtException.TYPE_INCONNUE, e);
        }
    }

    /**
     * @param agentId
     */
    @Override
    public List<RoleOrganisationnelDTOGWT> findListRoleOrganisationnelByAgentId(String agentId) throws SeditGwtException {
        try {
            return mapper.cloneToGwt(ucAvisAG.findListRoleOrganisationelsByAgentId(agentId), RoleOrganisationnelDTOGWT.class);
        } catch (BusinessException e) {
            if (SeditException.Type.MULTIPLE_EXCEPTION.equals(e.getType()))
                throw new SeditGwtException(e.getExceptionMap());
            else throw new SeditGwtException(SeditGwtException.TYPE_BUSINESS, e);
        } catch (TechnicalException e) {
            if (SeditException.Type.MULTIPLE_EXCEPTION.equals(e.getType()))
                throw new SeditGwtException(e.getExceptionMap());
            else throw new SeditGwtException(SeditGwtException.TYPE_TECHNICAL, e);
        } catch (Exception e) {
            throw new SeditGwtException(SeditGwtException.TYPE_INCONNUE, e);
        }
    }


    /**
     * @throws SeditGwtException
     * @see {@link IGwtServiceCaAvg#exclurePropositionAG(Integer)
     */
    @Override
    public void exclurePropositionAG(Integer propositionAGId) throws SeditGwtException {
        try {
            ucPropositionAG.exclureProposition(propositionAGId);
        } catch (BusinessException e) {
            if (SeditException.Type.MULTIPLE_EXCEPTION.equals(e.getType()))
                throw new SeditGwtException(e.getExceptionMap());
            else throw new SeditGwtException(SeditGwtException.TYPE_BUSINESS, e);
        } catch (TechnicalException e) {
            if (SeditException.Type.MULTIPLE_EXCEPTION.equals(e.getType()))
                throw new SeditGwtException(e.getExceptionMap());
            else throw new SeditGwtException(SeditGwtException.TYPE_TECHNICAL, e);
        } catch (Exception e) {
            throw new SeditGwtException(SeditGwtException.TYPE_INCONNUE, e);
        }
    }

    /**
     * @throws SeditGwtException
     * @see {@link IGwtServiceCaAvg#reintegrerPropositionAGNonPromouvable(Integer)
     */
    @Override
    public void reintegrerPropositionAGNonPromouvable(Integer propositionAGId) throws SeditGwtException {
        try {
            ucPropositionAG.reintegrerPropositionAGNonPromouvable(propositionAGId);
        } catch (BusinessException e) {
            if (SeditException.Type.MULTIPLE_EXCEPTION.equals(e.getType()))
                throw new SeditGwtException(e.getExceptionMap());
            else throw new SeditGwtException(SeditGwtException.TYPE_BUSINESS, e);
        } catch (TechnicalException e) {
            if (SeditException.Type.MULTIPLE_EXCEPTION.equals(e.getType()))
                throw new SeditGwtException(e.getExceptionMap());
            else throw new SeditGwtException(SeditGwtException.TYPE_TECHNICAL, e);
        } catch (Exception e) {
            throw new SeditGwtException(SeditGwtException.TYPE_INCONNUE, e);
        }
    }

    /**
     * @throws SeditGwtException
     * @see {@link IGwtServiceCaAvg#refuserPropositionAG(Integer, Date, String)
     */
    @Override
    public void refuserPropositionAG(Integer propositionAGId, Date dateRefus, String motifRefus) throws SeditGwtException {
        try {
            ucPropositionAG.refuserPropositionAG(propositionAGId, dateRefus, motifRefus);
        } catch (BusinessException e) {
            if (SeditException.Type.MULTIPLE_EXCEPTION.equals(e.getType()))
                throw new SeditGwtException(e.getExceptionMap());
            else throw new SeditGwtException(SeditGwtException.TYPE_BUSINESS, e);
        } catch (TechnicalException e) {
            if (SeditException.Type.MULTIPLE_EXCEPTION.equals(e.getType()))
                throw new SeditGwtException(e.getExceptionMap());
            else throw new SeditGwtException(SeditGwtException.TYPE_TECHNICAL, e);
        } catch (Exception e) {
            throw new SeditGwtException(SeditGwtException.TYPE_INCONNUE, e);
        }
    }

    /**
     * @see fr.bl.client.grh.ca.avg.remote.IGwtServiceCaAvg#findConditionEtDetailByConditionId(java.lang.Integer)
     */
    @Override
    public List<ConditionDePropositionEtDetailDTOGWT> findConditionEtDetailByConditionId(Integer conditionId) throws SeditGwtException {
        try {
            List<ConditionDePropositionEtDetailDTO> serverResult = ucPropositionAG.findConditionEtDetailByConditionId(conditionId);
            return mapper.cloneToGwt(serverResult, ConditionDePropositionEtDetailDTOGWT.class);
        } catch (BusinessException e) {
            if (SeditException.Type.MULTIPLE_EXCEPTION.equals(e.getType()))
                throw new SeditGwtException(e.getExceptionMap());
            else throw new SeditGwtException(SeditGwtException.TYPE_BUSINESS, e);
        } catch (TechnicalException e) {
            if (SeditException.Type.MULTIPLE_EXCEPTION.equals(e.getType()))
                throw new SeditGwtException(e.getExceptionMap());
            else throw new SeditGwtException(SeditGwtException.TYPE_TECHNICAL, e);
        } catch (Exception e) {
            throw new SeditGwtException(SeditGwtException.TYPE_INCONNUE, e);
        }
    }

    /**
     * @throws SeditGwtException
     * @see {@link IGwtServiceCaAvg#excluePropositionManuellement(Integer, String)
     */
    @Override
    public void excluePropositionManuellement(Integer propositionId, String motif) throws SeditGwtException {
        try {
            ucPropositionAG.excluePropositionManuellement(propositionId, motif);
        } catch (BusinessException e) {
            if (SeditException.Type.MULTIPLE_EXCEPTION.equals(e.getType()))
                throw new SeditGwtException(e.getExceptionMap());
            else throw new SeditGwtException(SeditGwtException.TYPE_BUSINESS, e);
        } catch (TechnicalException e) {
            if (SeditException.Type.MULTIPLE_EXCEPTION.equals(e.getType()))
                throw new SeditGwtException(e.getExceptionMap());
            else throw new SeditGwtException(SeditGwtException.TYPE_TECHNICAL, e);
        } catch (Exception e) {
            throw new SeditGwtException(SeditGwtException.TYPE_INCONNUE, e);
        }
    }

    /**
     * @throws SeditGwtException
     * @see {@link IGwtServiceCaAvg#findListAnciennete(String, Long)
     */
    @Override
    public List<GrilleAncienneteDTOGWT> findListAnciennete(String carriereId, Long codeRegroupement) throws SeditGwtException {
        try {
            List<GrilleAncienneteDTO> serverResult = ucGrilleAnciennete.findListeAnciennete(carriereId, codeRegroupement);
            return mapper.cloneToGwt(serverResult, GrilleAncienneteDTOGWT.class);
        } catch (BusinessException e) {
            if (SeditException.Type.MULTIPLE_EXCEPTION.equals(e.getType()))
                throw new SeditGwtException(e.getExceptionMap());
            else throw new SeditGwtException(SeditGwtException.TYPE_BUSINESS, e);
        } catch (TechnicalException e) {
            if (SeditException.Type.MULTIPLE_EXCEPTION.equals(e.getType()))
                throw new SeditGwtException(e.getExceptionMap());
            else throw new SeditGwtException(SeditGwtException.TYPE_TECHNICAL, e);
        } catch (Exception e) {
            throw new SeditGwtException(SeditGwtException.TYPE_INCONNUE, e);
        }
    }

    /**
     * @throws SeditGwtException
     * @see {@link IGwtServiceCaAvg#computeAndSaveGrilleAnciennete(String, Long)
     */
    @Override
    public GrilleAncienneteGWT computeAndSaveGrilleAnciennete(String carriereId, Long codeRegroupement) throws SeditGwtException {
        try {
            GrilleAnciennete grille = ucGrilleAnciennete.computeAndSaveGrilleAnciennete(carriereId, codeRegroupement);
            if (grille == null) return null;
            return mapper.cloneToGwt(grille, GrilleAncienneteGWT.class);

        } catch (BusinessException e) {
            if (SeditException.Type.MULTIPLE_EXCEPTION.equals(e.getType()))
                throw new SeditGwtException(e.getExceptionMap());
            else throw new SeditGwtException(SeditGwtException.TYPE_BUSINESS, e);
        } catch (TechnicalException e) {
            if (SeditException.Type.MULTIPLE_EXCEPTION.equals(e.getType()))
                throw new SeditGwtException(e.getExceptionMap());
            else throw new SeditGwtException(SeditGwtException.TYPE_TECHNICAL, e);
        } catch (Exception e) {
            throw new SeditGwtException(SeditGwtException.TYPE_INCONNUE, e);
        }
    }


    /**
     * @throws SeditGwtException
     * @see {@link IGwtServiceCaAvg#controleListPropositionsAGAvantInjection(List)
     */
    @Override
    public void controleListPropositionsAGAvantInjection(List<CollectiviteGWT> collectivites) throws SeditGwtException {
        try {
            List<Collectivite> collect = mapper.cloneToModel(collectivites, Collectivite.class);
            ucInjectionAG.controleListPropositionsAGAvantInjection(collect);
        } catch (BusinessException e) {
            if (SeditException.Type.MULTIPLE_EXCEPTION.equals(e.getType()))
                throw new SeditGwtException(e.getExceptionMap());
            else throw new SeditGwtException(SeditGwtException.TYPE_BUSINESS, e);
        } catch (TechnicalException e) {
            if (SeditException.Type.MULTIPLE_EXCEPTION.equals(e.getType()))
                throw new SeditGwtException(e.getExceptionMap());
            else throw new SeditGwtException(SeditGwtException.TYPE_TECHNICAL, e);
        } catch (Exception e) {
            throw new SeditGwtException(SeditGwtException.TYPE_INCONNUE, e);
        }
    }

    /**
     * @throws SeditGwtException
     * @see {@link IGwtServiceCaAvg#injectPropositionsAGById(List, UtilisateurSMRHGWT)
     */
    @Override
    public InjectionFicheCarriereRetourDTO injectPropositionsAGById(final List<Integer> listPropositionsId, UtilisateurSMRHGWT currentUserGWT) throws SeditGwtException {
        try {
            currentUserGWT.setContext(null);
            UtilisateurSMRH currentUser = mapper.cloneToModel(currentUserGWT, UtilisateurSMRH.class);
            // STH - 10/12/2010 - La carrière synthétique ne se mettait pas à jour
            InjectionFicheCarriereRetourAVGDTO resultat = ucInjectionAG.injectPropositionsAG(listPropositionsId, currentUser);
            ucInjectionAG.updateCarriereSynthetique(resultat.getListAVG());
            //-- AMA GPEC on lance le calcul des occupations poste et consommation poste budgetaire
            //-- car lorsqu'on modifie une position admin on relance les calculs
            this.ucPosteBudgetaireARecalculer.launchCalculConsommationPoste();
            this.ucPosteTravailOccupation.launchCalculTauxOccupationPoste();
            return resultat.getResultatInjection();
        } catch (BusinessException e) {
            if (SeditException.Type.MULTIPLE_EXCEPTION.equals(e.getType()))
                throw new SeditGwtException(e.getExceptionMap());
            else throw new SeditGwtException(SeditGwtException.TYPE_BUSINESS, e);
        } catch (TechnicalException e) {
            if (SeditException.Type.MULTIPLE_EXCEPTION.equals(e.getType()))
                throw new SeditGwtException(e.getExceptionMap());
            else throw new SeditGwtException(SeditGwtException.TYPE_TECHNICAL, e);
        } catch (Exception e) {
            throw new SeditGwtException(SeditGwtException.TYPE_INCONNUE, e);
        }
    }

    @Override
    public PropositionAGGWT simulPropositionAndReturnPropositionAG(String carriereId, Long codeRegroupement) throws SeditGwtException {
        try {
            final List<EnumTypeAvancementAG> listTypeAvancementAG = new ArrayList<EnumTypeAvancementAG>(3);
            listTypeAvancementAG.add(EnumTypeAvancementAG.GRADE);
            listTypeAvancementAG.add(EnumTypeAvancementAG.PROMOTION);
            listTypeAvancementAG.add(EnumTypeAvancementAG.RECLASSEMENT);
            PropositionAG propositionAG = ucPropositionAG.simulPropositionAndReturnPropositionAG(carriereId, codeRegroupement, null, null, listTypeAvancementAG);
            return mapper.cloneToGwt(propositionAG, PropositionAGGWT.class);
        } catch (BusinessException e) {
            if (SeditException.Type.MULTIPLE_EXCEPTION.equals(e.getType()))
                throw new SeditGwtException(e.getExceptionMap());
            else throw new SeditGwtException(SeditGwtException.TYPE_BUSINESS, e);
        } catch (TechnicalException e) {
            if (SeditException.Type.MULTIPLE_EXCEPTION.equals(e.getType()))
                throw new SeditGwtException(e.getExceptionMap());
            else throw new SeditGwtException(SeditGwtException.TYPE_TECHNICAL, e);
        } catch (Exception e) {
            throw new SeditGwtException(SeditGwtException.TYPE_INCONNUE, e);
        }
    }

    /**
     * Recherche la liste des propositions simulées pour la carriere et le code regroupement donné
     *
     * @param id
     * @param codeRegroupement
     * @throws SeditGwtException return List<Integer>
     */
    @Override
    public List<Integer> findListPropositionIdSimule(String carriereId, Long codeRegroupement) throws SeditGwtException {
        try {
            return ucPropositionAG.findListSimulePropositionId(carriereId, codeRegroupement);

        } catch (BusinessException e) {
            if (SeditException.Type.MULTIPLE_EXCEPTION.equals(e.getType()))
                throw new SeditGwtException(e.getExceptionMap());
            else throw new SeditGwtException(SeditGwtException.TYPE_BUSINESS, e);
        } catch (TechnicalException e) {
            if (SeditException.Type.MULTIPLE_EXCEPTION.equals(e.getType()))
                throw new SeditGwtException(e.getExceptionMap());
            else throw new SeditGwtException(SeditGwtException.TYPE_TECHNICAL, e);
        } catch (Exception e) {
            throw new SeditGwtException(SeditGwtException.TYPE_INCONNUE, e);
        }
    }

    /**
     * @param tableauAGGWT
     * @throws SeditGwtException
     * @throws SeditGwtException
     * @see {@link IGwtServiceCaAvg#calculPropositionsAGForTableauAG(TableauAGGWT, GradeGWT)
     */
    @Override
    public void calculPropositionsAGForTableauAG(TableauAGGWT tableauAGGWT, GradeGWT gradeCibleGWT) throws SeditGwtException {
        try {
            TableauAG tableau = mapper.cloneToModel(tableauAGGWT, TableauAG.class);
            Grade gradeCible = null;
            if (gradeCibleGWT != null) {
                gradeCible = mapper.cloneToModel(gradeCibleGWT, Grade.class);
            }
            final List<EnumTypeAvancementAG> listTypeAvancementAG = new ArrayList<EnumTypeAvancementAG>(2);
            if (tableau.getTypeAG() != null && tableau.getTypeAG().booleanValue()) {
                listTypeAvancementAG.add(EnumTypeAvancementAG.GRADE);
            }
            if (tableau.getTypePI() != null && tableau.getTypePI().booleanValue()) {
                listTypeAvancementAG.add(EnumTypeAvancementAG.PROMOTION);
            }
            if (tableau.getTypeRE() != null && tableau.getTypeRE().booleanValue()) {
                listTypeAvancementAG.add(EnumTypeAvancementAG.RECLASSEMENT);
            }
            ucPropositionAG.calculPropositions(false, tableau.getOrganisme(), tableau.getDateDebut(), tableau.getDateFin(), tableau, listTypeAvancementAG, tableau.getDatePromotion(), tableau.getListCollectivite(), tableau.getListCategorie(), tableau.getListFiliere(), tableau.getListGroupeHierarchique(), tableau.getListStatut(), null, null, null, gradeCible, tableau.getUniteGestion());
        } catch (BusinessException e) {
            if (SeditException.Type.MULTIPLE_EXCEPTION.equals(e.getType()))
                throw new SeditGwtException(e.getExceptionMap());
            else throw new SeditGwtException(SeditGwtException.TYPE_BUSINESS, e);
        } catch (TechnicalException e) {
            if (SeditException.Type.MULTIPLE_EXCEPTION.equals(e.getType()))
                throw new SeditGwtException(e.getExceptionMap());
            else throw new SeditGwtException(SeditGwtException.TYPE_TECHNICAL, e);
        } catch (Exception e) {
            throw new SeditGwtException(SeditGwtException.TYPE_INCONNUE, e);
        }
    }

    /**
     * @param tableauAGGWT
     * @throws SeditGwtException
     * @throws SeditGwtException
     * @see {@link IGwtServiceCaAvg#reCalculTableauAG(TableauAGGWT, Map)
     */
    @Override
    public void reCalculTableauAG(TableauAGGWT tableauAGGWT, Map<String, Object> criteria) throws SeditGwtException {
        try {
            translateCriteria(criteria);
            TableauAG tableau = mapper.cloneToModel(tableauAGGWT, TableauAG.class);
            ucPropositionAG.reCalculPropositionsByCriteria(tableau, criteria);
        } catch (BusinessException e) {
            if (SeditException.Type.MULTIPLE_EXCEPTION.equals(e.getType()))
                throw new SeditGwtException(e.getExceptionMap());
            else throw new SeditGwtException(SeditGwtException.TYPE_BUSINESS, e);
        } catch (TechnicalException e) {
            if (SeditException.Type.MULTIPLE_EXCEPTION.equals(e.getType()))
                throw new SeditGwtException(e.getExceptionMap());
            else throw new SeditGwtException(SeditGwtException.TYPE_TECHNICAL, e);
        } catch (Exception e) {
            throw new SeditGwtException(SeditGwtException.TYPE_INCONNUE, e);
        }
    }

    /**
     * @param tableauAGGWT
     * @throws SeditGwtException
     * @throws SeditGwtException
     * @see {@link IGwtServiceCaAvg#calculPropositionsAGForTableauAG(TableauAGGWT)
     */
    @Override
    public void reCalculTableauAG(TableauAGGWT tableauAGGWT) throws SeditGwtException {
        try {
            TableauAG tableau = mapper.cloneToModel(tableauAGGWT, TableauAG.class);
            ucPropositionAG.reCalculPropositions(tableau);
        } catch (BusinessException e) {
            if (SeditException.Type.MULTIPLE_EXCEPTION.equals(e.getType()))
                throw new SeditGwtException(e.getExceptionMap());
            else throw new SeditGwtException(SeditGwtException.TYPE_BUSINESS, e);
        } catch (TechnicalException e) {
            if (SeditException.Type.MULTIPLE_EXCEPTION.equals(e.getType()))
                throw new SeditGwtException(e.getExceptionMap());
            else throw new SeditGwtException(SeditGwtException.TYPE_TECHNICAL, e);
        } catch (Exception e) {
            throw new SeditGwtException(SeditGwtException.TYPE_INCONNUE, e);
        }
    }

    /**
     * @see {@link IGwtServiceCaAvg#reCalculPropositions(TableauAGGWT, Set)}
     */
    @Override
    public Set<PropositionAGGWT> reCalculPropositions(TableauAGGWT tableauAGGWT, Set<PropositionAGGWT> listPropositionsAGARecalculer) throws SeditGwtException {
        try {
            TableauAG tableau = mapper.cloneToModel(tableauAGGWT, TableauAG.class);
            Set<PropositionAG> setprop = mapper.cloneToModel(listPropositionsAGARecalculer, PropositionAG.class);

            Set<PropositionAG> setPropReturned = ucPropositionAG.reCalculPropositions(tableau, setprop);
            // FIXME returned objects needed ?
            return mapper.cloneToGwt(setPropReturned, PropositionAGGWT.class);
        } catch (BusinessException e) {
            if (SeditException.Type.MULTIPLE_EXCEPTION.equals(e.getType()))
                throw new SeditGwtException(e.getExceptionMap());
            else throw new SeditGwtException(SeditGwtException.TYPE_BUSINESS, e);
        } catch (TechnicalException e) {
            if (SeditException.Type.MULTIPLE_EXCEPTION.equals(e.getType()))
                throw new SeditGwtException(e.getExceptionMap());
            else throw new SeditGwtException(SeditGwtException.TYPE_TECHNICAL, e);
        } catch (Exception e) {
            throw new SeditGwtException(SeditGwtException.TYPE_INCONNUE, e);
        }
    }

    /**
     * @see {@link IGwtServiceCaAvg#reCalculPropositionsById(TableauAGGWT, List)}
     */
    @Override
    public Set<PropositionAGGWT> reCalculPropositionsById(TableauAGGWT tableauAGGWT, List<Integer> listIdPropositionsAGARecalculer) throws SeditGwtException {
        try {
            TableauAG tableau = mapper.cloneToModel(tableauAGGWT, TableauAG.class);
            //Set setPropReturned = 
            ucPropositionAG.reCalculPropositionsById(tableau, listIdPropositionsAGARecalculer);
            //return mapper.cloneToGwt(setPropReturned, PropositionAGGWT.class);
            return null;

        } catch (BusinessException e) {
            if (SeditException.Type.MULTIPLE_EXCEPTION.equals(e.getType()))
                throw new SeditGwtException(e.getExceptionMap());
            else throw new SeditGwtException(SeditGwtException.TYPE_BUSINESS, e);
        } catch (TechnicalException e) {
            if (SeditException.Type.MULTIPLE_EXCEPTION.equals(e.getType()))
                throw new SeditGwtException(e.getExceptionMap());
            else throw new SeditGwtException(SeditGwtException.TYPE_TECHNICAL, e);
        } catch (Exception e) {
            throw new SeditGwtException(SeditGwtException.TYPE_INCONNUE, e);
        }
    }

    @Override
    public void deleteProposition(PropositionAGGWT propositionAG) throws SeditGwtException {
        SeditGwtTry.run(() -> this.ucPropositionAG.deleteProposition(mapper.cloneToModel(propositionAG, PropositionAG.class)));

    }

    /**
     * @see {@link IGwtServiceCaAvg#deleteAllPropositionsByTableauAG(TableauAGGWT)}
     */
    @Override
    public void deleteAllPropositionsByTableauAG(TableauAGGWT tableauAGGWT) throws SeditGwtException {
        try {
            TableauAG tableau = mapper.cloneToModel(tableauAGGWT, TableauAG.class);
            ucPropositionAG.deletePropositionsByTableau(tableau);

        } catch (BusinessException e) {
            if (SeditException.Type.MULTIPLE_EXCEPTION.equals(e.getType()))
                throw new SeditGwtException(e.getExceptionMap());
            else throw new SeditGwtException(SeditGwtException.TYPE_BUSINESS, e);
        } catch (TechnicalException e) {
            if (SeditException.Type.MULTIPLE_EXCEPTION.equals(e.getType()))
                throw new SeditGwtException(e.getExceptionMap());
            else throw new SeditGwtException(SeditGwtException.TYPE_TECHNICAL, e);
        } catch (Exception e) {
            throw new SeditGwtException(SeditGwtException.TYPE_INCONNUE, e);
        }
    }

    /**
     * @see {@link IGwtServiceCaAvg#deletePropositionsByTableauAGAndGradeCible(TableauAGGWT, GradeGWT)}
     */
    @Override
    public void deletePropositionsByTableauAGAndGradeCible(TableauAGGWT tableauAGGWT, GradeGWT gradeCibleGWT) throws SeditGwtException {
        try {
            TableauAG tableau = mapper.cloneToModel(tableauAGGWT, TableauAG.class);
            Grade gradeCible = mapper.cloneToModel(gradeCibleGWT, Grade.class);
            ucPropositionAG.deletePropositionsByTableauAGAndGradeCible(tableau, gradeCible);

        } catch (BusinessException e) {
            if (SeditException.Type.MULTIPLE_EXCEPTION.equals(e.getType()))
                throw new SeditGwtException(e.getExceptionMap());
            else throw new SeditGwtException(SeditGwtException.TYPE_BUSINESS, e);
        } catch (TechnicalException e) {
            if (SeditException.Type.MULTIPLE_EXCEPTION.equals(e.getType()))
                throw new SeditGwtException(e.getExceptionMap());
            else throw new SeditGwtException(SeditGwtException.TYPE_TECHNICAL, e);
        } catch (Exception e) {
            throw new SeditGwtException(SeditGwtException.TYPE_INCONNUE, e);
        }
    }

    @Override
    public void deleteAllPropositionsSimulation() throws SeditGwtException {
        SeditGwtTry.run(() -> this.ucPropositionAG.deleteAllPropositionsSimulation());

    }

    /**
     * @see {@link IGwtServiceCaAvg#findListServiceDTObyCollectivite(String)}
     */
    @Override
    public List<ServiceDTOGWT> findListServiceDTOByCollectivite(String collectiviteId) throws SeditGwtException {
        try {

            List<ServiceDTO> serverList = ucService4Grade.findListServiceDTOByCollectivite(collectiviteId, new Date());
            return mapper.cloneToGwt(serverList, ServiceDTOGWT.class);
        } catch (BusinessException e) {
            if (SeditException.Type.MULTIPLE_EXCEPTION.equals(e.getType()))
                throw new SeditGwtException(e.getExceptionMap());
            else throw new SeditGwtException(SeditGwtException.TYPE_BUSINESS, e);
        } catch (TechnicalException e) {
            if (SeditException.Type.MULTIPLE_EXCEPTION.equals(e.getType()))
                throw new SeditGwtException(e.getExceptionMap());
            else throw new SeditGwtException(SeditGwtException.TYPE_TECHNICAL, e);
        } catch (Exception e) {
            throw new SeditGwtException(SeditGwtException.TYPE_INCONNUE, e);
        }
    }

    /**
     * @see IGwtServiceCaAvg#simulePropositionsSurConditions(HashMap)
     */
    @Override
    public List<PropositionSimuleDTOGWT> simulePropositionsSurConditions(HashMap<String, Object> mapCriteres) throws SeditGwtException {
        try {
            List<PropositionSimuleDTO> listServer = ucPropositionAG.simulePropositionSurConditions(mapCriteres, new Date(), null);
            return mapper.cloneToGwt(listServer, PropositionSimuleDTOGWT.class);

        } catch (BusinessException e) {
            if (SeditException.Type.MULTIPLE_EXCEPTION.equals(e.getType()))
                throw new SeditGwtException(e.getExceptionMap());
            else throw new SeditGwtException(SeditGwtException.TYPE_BUSINESS, e);
        } catch (TechnicalException e) {
            if (SeditException.Type.MULTIPLE_EXCEPTION.equals(e.getType()))
                throw new SeditGwtException(e.getExceptionMap());
            else throw new SeditGwtException(SeditGwtException.TYPE_TECHNICAL, e);
        } catch (Exception e) {
            throw new SeditGwtException(SeditGwtException.TYPE_INCONNUE, e);
        }
    }

    /**
     * @param criteria
     * @return HashMap
     * @throws Exception
     * @see GwtServiceCAAvg#countConcoursAgent(HashMap, String)
     * @see GwtServiceCAAvg#findListConcoursAgent(HashMap, HashMap, long, long,
     * String, boolean)
     */
    private HashMap<String, Object> translateCriteriaForPromotionsConcoursAgent(HashMap<String, Object> criteria) throws Exception {

        final HashMap<String, Object> mapCriteria = new HashMap<String, Object>(criteria);
        mapCriteria.remove("dateDebutObtention");
        mapCriteria.remove("dateFinObtention");
        mapCriteria.remove("EnumDecisionCAP");
        // dateObtention, récupération des dates en string dd/mm/yyyy 
        // pour la compatibilité avec l'export
        Date dateDebutObtention = null;
        String date = (String) criteria.get("dateDebutObtention");
        //dateDebutObtention
        if (date != null) {
            dateDebutObtention = UtilsDate.stringToDate(date);
            mapCriteria.put("dateDebutObtention", dateDebutObtention);
        }

        Date dateFinObtention = null;
        date = (String) criteria.get("dateFinObtention");
        //dateDebutObtention
        if (date != null) {
            dateFinObtention = UtilsDate.stringToDate(date);
            mapCriteria.put("dateFinObtention", dateFinObtention);
        }

        EnumDecisionCAPGWT decision = (EnumDecisionCAPGWT) criteria.get("EnumDecisionCAP");
        if (decision != null) {
            mapCriteria.put("EnumDecisionCAP", mapper.cloneToModel(decision, EnumDecisionCAP.class));
        }

        return mapCriteria;
    }

    @Override
    public Long countPromotionsConcoursAgent(HashMap<String, Object> criteria, String filter) throws SeditGwtException {
        try {
            criteria = translateCriteriaForPromotionsConcoursAgent(criteria);
            return ucPropositionAG.countPromotionsConcoursAgent(criteria, filter);
        } catch (BusinessException e) {
            if (SeditException.Type.MULTIPLE_EXCEPTION.equals(e.getType()))
                throw new SeditGwtException(e.getExceptionMap());
            else throw new SeditGwtException(SeditGwtException.TYPE_BUSINESS, e);
        } catch (TechnicalException e) {
            if (SeditException.Type.MULTIPLE_EXCEPTION.equals(e.getType()))
                throw new SeditGwtException(e.getExceptionMap());
            else throw new SeditGwtException(SeditGwtException.TYPE_TECHNICAL, e);
        } catch (Exception e) {
            throw new SeditGwtException(SeditGwtException.TYPE_INCONNUE, e);
        }
    }


    @Override
    public List<PropositionsConcoursAgentDTOGWT> findListPromotionsConcoursAgent(Map<String, Integer> tri, HashMap<String, Object> criteria, long firstLine, long limitLine, String filter, boolean showMoreColumns) throws SeditGwtException {
        try {
            criteria = translateCriteriaForPromotionsConcoursAgent(criteria);

            List<PropositionsConcoursAgentDTO> serverList = ucPropositionAG.findListPromotionsConcoursAgent(tri, criteria, firstLine, limitLine, filter);
            if (serverList == null) return new ArrayList<PropositionsConcoursAgentDTOGWT>();

            return mapper.cloneToGwt(serverList, PropositionsConcoursAgentDTOGWT.class);
        } catch (BusinessException e) {
            if (SeditException.Type.MULTIPLE_EXCEPTION.equals(e.getType()))
                throw new SeditGwtException(e.getExceptionMap());
            else throw new SeditGwtException(SeditGwtException.TYPE_BUSINESS, e);
        } catch (TechnicalException e) {
            if (SeditException.Type.MULTIPLE_EXCEPTION.equals(e.getType()))
                throw new SeditGwtException(e.getExceptionMap());
            else throw new SeditGwtException(SeditGwtException.TYPE_TECHNICAL, e);
        } catch (Exception e) {
            throw new SeditGwtException(SeditGwtException.TYPE_INCONNUE, e);
        }
    }

    @Override
    public void officialisePromotion(Integer propositionId, Boolean promu) throws SeditGwtException {
        try {
            ucPropositionAG.officialisePromotion(propositionId, promu);
        } catch (BusinessException e) {
            if (SeditException.Type.MULTIPLE_EXCEPTION.equals(e.getType()))
                throw new SeditGwtException(e.getExceptionMap());
            else throw new SeditGwtException(SeditGwtException.TYPE_BUSINESS, e);
        } catch (TechnicalException e) {
            if (SeditException.Type.MULTIPLE_EXCEPTION.equals(e.getType()))
                throw new SeditGwtException(e.getExceptionMap());
            else throw new SeditGwtException(SeditGwtException.TYPE_TECHNICAL, e);
        } catch (Exception e) {
            throw new SeditGwtException(SeditGwtException.TYPE_INCONNUE, e);
        }
    }

    @Override
    public ImprimeDTOGWT getImprimeDTOByPropositionId(Integer propositionId) throws SeditGwtException {
        try {
            return this.mapper.cloneToGwt(this.ucImprimeAG.loadImprimeDTOForPropositionAGId(propositionId), ImprimeDTOGWT.class);
        } catch (BusinessException e) {
            if (SeditException.Type.MULTIPLE_EXCEPTION.equals(e.getType()))
                throw new SeditGwtException(e.getExceptionMap());
            else throw new SeditGwtException(SeditGwtException.TYPE_BUSINESS, e);
        } catch (TechnicalException e) {
            if (SeditException.Type.MULTIPLE_EXCEPTION.equals(e.getType()))
                throw new SeditGwtException(e.getExceptionMap());
            else throw new SeditGwtException(SeditGwtException.TYPE_TECHNICAL, e);
        } catch (Exception e) {
            throw new SeditGwtException(SeditGwtException.TYPE_INCONNUE, e);
        }
    }

    @Override
    public ImprimeDTOGWT getImprimeDTOForSPVByPropositionId(Integer propositionId) throws SeditGwtException {
        try {
            return this.mapper.cloneToGwt(this.ucImprimeAG.loadImprimeDTOForSPVPropositionAGId(propositionId), ImprimeDTOGWT.class);
        } catch (BusinessException e) {
            if (SeditException.Type.MULTIPLE_EXCEPTION.equals(e.getType()))
                throw new SeditGwtException(e.getExceptionMap());
            else throw new SeditGwtException(SeditGwtException.TYPE_BUSINESS, e);
        } catch (TechnicalException e) {
            if (SeditException.Type.MULTIPLE_EXCEPTION.equals(e.getType()))
                throw new SeditGwtException(e.getExceptionMap());
            else throw new SeditGwtException(SeditGwtException.TYPE_TECHNICAL, e);
        } catch (Exception e) {
            throw new SeditGwtException(SeditGwtException.TYPE_INCONNUE, e);
        }
    }


    @Override
    public ContexteExecSignetPersoGWT loadContexteExecSignetPersoByPropositionAG(List<Integer> listIdProposition, final String modeleTypeId) throws SeditGwtException {
        return SeditGwtTry.get(() -> {
            final ContexteExecSignetPerso contexte = this.ucImprimeAG.loadContexteExecSignetPersoByPropositionAG(listIdProposition, modeleTypeId);
            return mapper.cloneToGwt(contexte, ContexteExecSignetPersoGWT.class);
        });
    }

    @Override
    public ContexteExecSignetPersoGWT loadContexteExecSignetPersoForSPVByPropositionAG(List<Integer> listIdProposition, final String modeleTypeId) throws SeditGwtException {
        return SeditGwtTry.get(() -> {
            final ContexteExecSignetPerso contexte = this.ucImprimeAG.loadContexteExecSignetPersoForSPVByPropositionAG(listIdProposition, modeleTypeId);
            return mapper.cloneToGwt(contexte, ContexteExecSignetPersoGWT.class);
        });
    }

    @Override
    public ContexteExecSignetPersoGWT loadContexteExecSignetPersoByTableauAG(TableauAGGWT tableau, final String modeleTypeId) throws SeditGwtException {
        return SeditGwtTry.get(() -> {
            final ContexteExecSignetPerso contexte = this.ucImprimeAG.loadContexteExecSignetPersoByTableauAG(mapper.cloneToModel(tableau, TableauAG.class), modeleTypeId);
            return mapper.cloneToGwt(contexte, ContexteExecSignetPersoGWT.class);
        });
    }

    @Override
    public ContexteExecSignetPersoGWT loadContexteExecSignetPersoByCriteriaAG(Map<String, Object> criteria, String modeleTypeId) throws SeditGwtException {
        return SeditGwtTry.get(() -> {
            translateCriteria(criteria);
            final ContexteExecSignetPerso contexte = this.ucImprimeAG.loadContexteExecSignetPersoByCriteriaAG(criteria, modeleTypeId);
            return mapper.cloneToGwt(contexte, ContexteExecSignetPersoGWT.class);
        });
    }

    @Override
    public ContexteExecSignetPersoGWT loadContexteExecSignetPerso4InjectAG(CollectiviteGWT collectivite, final String modeleTypeId) throws SeditGwtException {
        return SeditGwtTry.get(() -> {
            final ContexteExecSignetPerso contexte = this.ucImprimeAG.loadContexteExecSignetPerso4InjectAG(mapper.cloneToModel(collectivite, Collectivite.class), modeleTypeId);
            return mapper.cloneToGwt(contexte, ContexteExecSignetPersoGWT.class);
        });
    }

    @Override
    public ImprimeDTOGWT saveArreteAG(Integer propositionId, String modeleTypeId, Date dateGeneration, Boolean officiel, String numeroArrete, boolean launchEdition, ContexteExecSignetPersoGWT contexteExecSignetPersoGWT) throws SeditGwtException {
        try {
            return this.mapper.cloneToGwt(this.ucImprimeAG.saveImprimeAG(propositionId, modeleTypeId, dateGeneration, officiel, numeroArrete, true, launchEdition, false, mapper.cloneToModel(contexteExecSignetPersoGWT, ContexteExecSignetPerso.class)), ImprimeDTOGWT.class);
        }
        // Pour le cas du modèle de document non trouvé indiquer exception business afin de l'afficher comme tel
        catch (FusionException se) {
            throw new SeditGwtException(FusionException.Type.OPEN_OFFICE_BAD_MODEL.equals(se.getType()) ? SeditGwtException.TYPE_BUSINESS : SeditGwtException.TYPE_TECHNICAL, se);
        } catch (BusinessException e) {
            if (SeditException.Type.MULTIPLE_EXCEPTION.equals(e.getType()))
                throw new SeditGwtException(e.getExceptionMap());
            else throw new SeditGwtException(SeditGwtException.TYPE_BUSINESS, e);
        } catch (TechnicalException e) {
            if (SeditException.Type.MULTIPLE_EXCEPTION.equals(e.getType()))
                throw new SeditGwtException(e.getExceptionMap());
            else throw new SeditGwtException(SeditGwtException.TYPE_TECHNICAL, e);
        } catch (Exception e) {
            throw new SeditGwtException(SeditGwtException.TYPE_INCONNUE, e);
        }
    }

    @Override
    public ImprimeDTOGWT saveArreteSPVAG(Integer propositionId, String modeleTypeId, Date dateGeneration, Boolean officiel, String numeroArrete, boolean launchEdition, ContexteExecSignetPersoGWT contexteExecSignetPersoGWT) throws SeditGwtException {
        try {
            return this.mapper.cloneToGwt(this.ucImprimeAG.saveImprimeSPVAG(propositionId, modeleTypeId, dateGeneration, officiel, numeroArrete, true, launchEdition, false, mapper.cloneToModel(contexteExecSignetPersoGWT, ContexteExecSignetPerso.class)), ImprimeDTOGWT.class);
        }
        // Pour le cas du modèle de document non trouvé indiquer exception business afin de l'afficher comme tel
        catch (FusionException se) {
            throw new SeditGwtException(FusionException.Type.OPEN_OFFICE_BAD_MODEL.equals(se.getType()) ? SeditGwtException.TYPE_BUSINESS : SeditGwtException.TYPE_TECHNICAL, se);
        } catch (BusinessException e) {
            if (SeditException.Type.MULTIPLE_EXCEPTION.equals(e.getType()))
                throw new SeditGwtException(e.getExceptionMap());
            else throw new SeditGwtException(SeditGwtException.TYPE_BUSINESS, e);
        } catch (TechnicalException e) {
            if (SeditException.Type.MULTIPLE_EXCEPTION.equals(e.getType()))
                throw new SeditGwtException(e.getExceptionMap());
            else throw new SeditGwtException(SeditGwtException.TYPE_TECHNICAL, e);
        } catch (Exception e) {
            throw new SeditGwtException(SeditGwtException.TYPE_INCONNUE, e);
        }
    }

    @Override
    public RetourEtListeErreurDTOGWT saveArreteAGBatch(final List<Integer> propositionIds, final String modeleTypeId, final Date dateDecision, final Boolean officiel, final String numeroArretePrefix, final Long numeroArreteIndex, final ContexteExecSignetPersoGWT contexteExecSignetPersoGWT, final Boolean editerDejaExistant, final NumeroArreteDTO startNumber) throws SeditGwtException {
        try {
            return mapper.cloneToGwt(this.ucImprimeAG.saveImprimeAGBatch(propositionIds, modeleTypeId, dateDecision, officiel, numeroArretePrefix, numeroArreteIndex, mapper.cloneToModel(contexteExecSignetPersoGWT, ContexteExecSignetPerso.class), editerDejaExistant, startNumber), RetourEtListeErreurDTOGWT.class);
        } catch (BusinessException e) {
            if (SeditException.Type.MULTIPLE_EXCEPTION.equals(e.getType()))
                throw new SeditGwtException(e.getExceptionMap());
            else throw new SeditGwtException(SeditGwtException.TYPE_BUSINESS, e);
        } catch (TechnicalException e) {
            if (SeditException.Type.MULTIPLE_EXCEPTION.equals(e.getType()))
                throw new SeditGwtException(e.getExceptionMap());
            else throw new SeditGwtException(SeditGwtException.TYPE_TECHNICAL, e);
        } catch (Exception e) {
            throw new SeditGwtException(SeditGwtException.TYPE_INCONNUE, e);
        }
    }

    @Override
    public void saveArreteAGBatch(final Map<String, Object> criteria, final String userId, final String modeleTypeId, final Date dateDecision, final Boolean officiel, final String numeroArretePrefix, final Long numeroArreteIndex, final ContexteExecSignetPersoGWT contexteExecSignetPersoGWT, final Boolean editerDejaExistant, final NumeroArreteDTO startNumber) throws SeditGwtException {
        try {
            translateCriteria(criteria);
            // récupération du job à lancer
            ApplicationContext context = ContextLoader.getCurrentWebApplicationContext();
            ITaskSaveImprimeAGBatch task = (ITaskSaveImprimeAGBatch) context.getBean("taskSaveImprimeAGBatch");
            // initialisation des paramètres sur le job
            SaveArreteAgBatchParams params = new SaveArreteAgBatchParams(criteria, modeleTypeId, dateDecision, officiel, numeroArretePrefix, numeroArreteIndex, this.mapper.cloneToModel(contexteExecSignetPersoGWT, ContexteExecSignetPerso.class), editerDejaExistant, startNumber);
            task.setUser(userId);
            task.setParams(params);

            // lancement du batch
            final IBatchManager batchManager = (IBatchManager) context.getBean("coreBatchManager");
            final IJobCommandInfo jobInfo = batchManager.addJob(task, BatchModeEnum.LAUNCH_ON_CALL);

        } catch (BusinessException e) {
            if (SeditException.Type.MULTIPLE_EXCEPTION.equals(e.getType()))
                throw new SeditGwtException(e.getExceptionMap());
            else throw new SeditGwtException(SeditGwtException.TYPE_BUSINESS, e);
        } catch (TechnicalException e) {
            if (SeditException.Type.MULTIPLE_EXCEPTION.equals(e.getType()))
                throw new SeditGwtException(e.getExceptionMap());
            else throw new SeditGwtException(SeditGwtException.TYPE_TECHNICAL, e);
        } catch (Exception e) {
            throw new SeditGwtException(SeditGwtException.TYPE_INCONNUE, e);
        }
    }

    @Override
    public RetourEtListeErreurDTOGWT saveArreteSPVAGBatch(List<Integer> propositionIds, String modeleTypeId, Date dateDecision, Boolean officiel, String numeroArretePrefix, Long numeroArreteIndex, NumeroArreteDTO numeroArreteDTO, ContexteExecSignetPersoGWT contexteExecSignetPersoGWT, Boolean editerDejaExistant) throws SeditGwtException {
        try {
            return mapper.cloneToGwt(this.ucImprimeAG.saveImprimeSPVAGBatch(propositionIds, modeleTypeId, dateDecision, officiel, numeroArretePrefix, numeroArreteIndex, numeroArreteDTO, mapper.cloneToModel(contexteExecSignetPersoGWT, ContexteExecSignetPerso.class), editerDejaExistant), RetourEtListeErreurDTOGWT.class);
        } catch (BusinessException e) {
            if (SeditException.Type.MULTIPLE_EXCEPTION.equals(e.getType()))
                throw new SeditGwtException(e.getExceptionMap());
            else throw new SeditGwtException(SeditGwtException.TYPE_BUSINESS, e);
        } catch (TechnicalException e) {
            if (SeditException.Type.MULTIPLE_EXCEPTION.equals(e.getType()))
                throw new SeditGwtException(e.getExceptionMap());
            else throw new SeditGwtException(SeditGwtException.TYPE_TECHNICAL, e);
        } catch (Exception e) {
            throw new SeditGwtException(SeditGwtException.TYPE_INCONNUE, e);
        }
    }

    @Override
    public RetourEtListeErreurDTOGWT saveArreteAGTableau(final TableauAGGWT tableau, final String modeleTypeId, final Date dateDecision, final Boolean officiel, final String numeroArretePrefix, final Long numeroArreteIndex, final ContexteExecSignetPersoGWT contexteExecSignetPersoGWT, final Boolean editerDejaExistant, final NumeroArreteDTO startNumber) throws SeditGwtException {
        try {
            return mapper.cloneToGwt(this.ucImprimeAG.saveImprimeAG4Tableau(mapper.cloneToModel(tableau, TableauAG.class), modeleTypeId, dateDecision, officiel, numeroArretePrefix, numeroArreteIndex, mapper.cloneToModel(contexteExecSignetPersoGWT, ContexteExecSignetPerso.class), editerDejaExistant, startNumber), RetourEtListeErreurDTOGWT.class);
        } catch (BusinessException e) {
            if (SeditException.Type.MULTIPLE_EXCEPTION.equals(e.getType()))
                throw new SeditGwtException(e.getExceptionMap());
            else throw new SeditGwtException(SeditGwtException.TYPE_BUSINESS, e);
        } catch (TechnicalException e) {
            if (SeditException.Type.MULTIPLE_EXCEPTION.equals(e.getType()))
                throw new SeditGwtException(e.getExceptionMap());
            else throw new SeditGwtException(SeditGwtException.TYPE_TECHNICAL, e);
        } catch (Exception e) {
            throw new SeditGwtException(SeditGwtException.TYPE_INCONNUE, e);
        }
    }

    @Override
    public RetourEtListeErreurDTOGWT saveArreteAGBatch4Inject(final CollectiviteGWT collectivite, final String modeleTypeId, final Date dateDecision, final Boolean officiel, final String numeroArretePrefix, final Long numeroArreteIndex, final ContexteExecSignetPersoGWT contexteExecSignetPersoGWT, final Boolean editerDejaExistant, final NumeroArreteDTO startNumber) throws SeditGwtException {
        try {
            return mapper.cloneToGwt(this.ucImprimeAG.saveInjectableImprimeAG4Collectivite(mapper.cloneToModel(collectivite, Collectivite.class), modeleTypeId, dateDecision, officiel, numeroArretePrefix, numeroArreteIndex, mapper.cloneToModel(contexteExecSignetPersoGWT, ContexteExecSignetPerso.class), editerDejaExistant, startNumber), RetourEtListeErreurDTOGWT.class);
        } catch (BusinessException e) {
            if (SeditException.Type.MULTIPLE_EXCEPTION.equals(e.getType()))
                throw new SeditGwtException(e.getExceptionMap());
            else throw new SeditGwtException(SeditGwtException.TYPE_BUSINESS, e);
        } catch (TechnicalException e) {
            if (SeditException.Type.MULTIPLE_EXCEPTION.equals(e.getType()))
                throw new SeditGwtException(e.getExceptionMap());
            else throw new SeditGwtException(SeditGwtException.TYPE_TECHNICAL, e);
        } catch (Exception e) {
            throw new SeditGwtException(SeditGwtException.TYPE_INCONNUE, e);
        }
    }

    @Override
    public RetourEtListeErreurDTOGWT imprimerPropositionsBatch(String user, String rep, List<Integer> propositionIds) throws SeditGwtException {
        try {
            return mapper.cloneToGwt(this.ucImprimeAG.imprimerPropositionsBatch(user, rep, propositionIds), RetourEtListeErreurDTOGWT.class);
        } catch (BusinessException e) {
            if (SeditException.Type.MULTIPLE_EXCEPTION.equals(e.getType()))
                throw new SeditGwtException(e.getExceptionMap());
            else throw new SeditGwtException(SeditGwtException.TYPE_BUSINESS, e);
        } catch (TechnicalException e) {
            if (SeditException.Type.MULTIPLE_EXCEPTION.equals(e.getType()))
                throw new SeditGwtException(e.getExceptionMap());
            else throw new SeditGwtException(SeditGwtException.TYPE_TECHNICAL, e);
        } catch (Exception e) {
            throw new SeditGwtException(SeditGwtException.TYPE_INCONNUE, e);
        }
    }


    @Override
    public void imprimerPropositionsBatch(String userName, String userId, String rep, Map<String, Object> criteria) throws SeditGwtException {
        try {
            translateCriteria(criteria);

            // récupération du job à lancer
            ApplicationContext context = ContextLoader.getCurrentWebApplicationContext();
            ITaskImprimerArretes task = (ITaskImprimerArretes) context.getBean("taskImprimerArretes");
            // initialisation des paramètres sur le job
            task.setUser(userId);
            task.setUserName(userName);
            task.setCriteria(criteria);
            task.setRepertoire(rep);

            // lancement du batch
            IBatchManager batchManager = (IBatchManager) context.getBean("coreBatchManager");
            IJobCommandInfo jobInfo = batchManager.addJob(task, BatchModeEnum.LAUNCH_ON_CALL);
        } catch (BusinessException e) {
            if (SeditException.Type.MULTIPLE_EXCEPTION.equals(e.getType()))
                throw new SeditGwtException(e.getExceptionMap());
            else throw new SeditGwtException(SeditGwtException.TYPE_BUSINESS, e);
        } catch (TechnicalException e) {
            if (SeditException.Type.MULTIPLE_EXCEPTION.equals(e.getType()))
                throw new SeditGwtException(e.getExceptionMap());
            else throw new SeditGwtException(SeditGwtException.TYPE_TECHNICAL, e);
        } catch (Exception e) {
            throw new SeditGwtException(SeditGwtException.TYPE_INCONNUE, e);
        }
    }

    /**
     * @see IGwtServiceCaAvg#cloturerTacheWF(TacheWfEcheancierDTOGWT, String)
     */
    @Override
    public void cloturerTacheWF(TacheWfEcheancierDTOGWT task, String userId) throws SeditGwtException {
        try {
            TacheWfEcheancierDTO tacheWfEcheancierDTO = mapper.cloneToModel(task, TacheWfEcheancierDTO.class);

            ucTableauAG.cloturerTacheWF(tacheWfEcheancierDTO, userId);
        } catch (BusinessException be) {
            throw new SeditGwtException(SeditGwtException.TYPE_BUSINESS, be);
        } catch (SeditException se) {
            throw new SeditGwtException(SeditGwtException.TYPE_TECHNICAL, se);
        } catch (Throwable t) {
            throw new SeditGwtException(SeditGwtException.TYPE_INCONNUE, t);
        }
    }

    /**
     * @see IGwtServiceCaAvg#getCurrentTaskTableau(String, String)
     */
    @Override
    public TacheWfEcheancierDTOGWT getCurrentTaskTableau(String tableauId, String userId) throws SeditGwtException {
        try {
            TacheWfEcheancierDTO tacheWfEcheancierDTO = ucTableauAG.getCurrentTaskTableau(tableauId, userId);
            TacheWfEcheancierDTOGWT tacheWfEcheancierDTOgwt = mapper.cloneToGwt(tacheWfEcheancierDTO, TacheWfEcheancierDTOGWT.class);
            return tacheWfEcheancierDTOgwt;
        } catch (BusinessException be) {
            throw new SeditGwtException(SeditGwtException.TYPE_BUSINESS, be);
        } catch (SeditException se) {
            throw new SeditGwtException(SeditGwtException.TYPE_TECHNICAL, se);
        } catch (Throwable t) {
            throw new SeditGwtException(SeditGwtException.TYPE_INCONNUE, t);
        }
    }

    /**
     * @see IGwtServiceCaAvg#getDetailTache(String, String)
     */
    @Override
    public TacheWfEcheancierDTOGWT getDetailTache(String actorId, String taskName) throws SeditGwtException {
        try {
            TacheWfEcheancierDTO tacheWfEcheancierDTO = ucTableauAG.getDetailTache(actorId, taskName);
            TacheWfEcheancierDTOGWT tacheWfEcheancierDTOgwt = mapper.cloneToGwt(tacheWfEcheancierDTO, TacheWfEcheancierDTOGWT.class);
            return tacheWfEcheancierDTOgwt;
        } catch (BusinessException be) {
            throw new SeditGwtException(SeditGwtException.TYPE_BUSINESS, be);
        } catch (SeditException se) {
            throw new SeditGwtException(SeditGwtException.TYPE_TECHNICAL, se);
        } catch (Throwable t) {
            throw new SeditGwtException(SeditGwtException.TYPE_INCONNUE, t);
        }
    }

    /**
     * @see IGwtServiceCaAvg#getDetailTache(long)
     */
    @Override
    public TacheWfEcheancierDTOGWT getDetailTache(long taskId) throws SeditGwtException {
        try {
            TacheWfEcheancierDTO tacheWfEcheancierDTO = ucTableauAG.getDetailTache(taskId);
            TacheWfEcheancierDTOGWT tacheWfEcheancierDTOgwt = mapper.cloneToGwt(tacheWfEcheancierDTO, TacheWfEcheancierDTOGWT.class);
            return tacheWfEcheancierDTOgwt;
        } catch (BusinessException be) {
            throw new SeditGwtException(SeditGwtException.TYPE_BUSINESS, be);
        } catch (SeditException se) {
            throw new SeditGwtException(SeditGwtException.TYPE_TECHNICAL, se);
        } catch (Throwable t) {
            throw new SeditGwtException(SeditGwtException.TYPE_INCONNUE, t);
        }
    }

    /**
     * @see IGwtServiceCaAvg#saveTableauAndClotureTache(TableauAGGWT, TacheWfEcheancierDTOGWT, String)
     */
    @Override
    public TableauAGGWT saveTableauAndClotureTache(TableauAGGWT tableauAG, TacheWfEcheancierDTOGWT task, String userId) throws SeditGwtException {
        try {
            TableauAG serverTableau = mapper.cloneToModel(tableauAG, TableauAG.class);
            TacheWfEcheancierDTO tacheWfEcheancierDTO = mapper.cloneToModel(task, TacheWfEcheancierDTO.class);
            serverTableau = ucTableauAG.saveTableauAndClotureTache(serverTableau, tacheWfEcheancierDTO, userId);

            if (serverTableau == null) return null;
            else return mapper.cloneToGwt(serverTableau, TableauAGGWT.class);
        } catch (SeditException se) {
            throw new SeditGwtException(se instanceof BusinessException ? SeditGwtException.TYPE_BUSINESS : SeditGwtException.TYPE_TECHNICAL, se);
        } catch (Throwable t) {
            throw new SeditGwtException(SeditGwtException.TYPE_INCONNUE, t);
        }
    }

    /**
     * @see IGwtServiceCaAvg#startEcheancierCAP(List, TableauAGGWT, String)
     */
    @Override
    public void startEcheancierCAP(List<EcheanceCAPGWT> listeEcheances, TableauAGGWT tableauAG, String userId) throws SeditGwtException {
        try {
            List<EcheanceCAP> listecheanceCAP = mapper.cloneToModel(listeEcheances, EcheanceCAP.class);
            TableauAG tab = mapper.cloneToModel(tableauAG, TableauAG.class);
            ucTableauAG.startEcheancierCAP(listecheanceCAP, tab, userId);
        } catch (BusinessException be) {
            throw new SeditGwtException(SeditGwtException.TYPE_BUSINESS, be);
        } catch (SeditException se) {
            throw new SeditGwtException(SeditGwtException.TYPE_TECHNICAL, se);
        } catch (Throwable t) {
            throw new SeditGwtException(SeditGwtException.TYPE_INCONNUE, t);
        }
    }

    /**
     * @see IGwtServiceCaAvg#notifierParCourrielAG(TableauAGGWT)
     */
    @Override
    public void notifierParCourrielAG(TableauAGGWT tableau) throws SeditGwtException {
        try {
            TableauAG tab = mapper.cloneToModel(tableau, TableauAG.class);
            ucTableauAG.notifierParCourrielAG(tab);
        } catch (BusinessException be) {
            throw new SeditGwtException(SeditGwtException.TYPE_BUSINESS, be);
        } catch (SeditException se) {
            throw new SeditGwtException(SeditGwtException.TYPE_TECHNICAL, se);
        } catch (Throwable t) {
            throw new SeditGwtException(SeditGwtException.TYPE_INCONNUE, t);
        }
    }

    /**
     * @see IGwtServiceCaAvg#createCourrierBatch(TableauAGGWT, String, Date, String, Long)
     */
    @Override
    public void createCourrierBatch(TableauAGGWT tableau, String modeleTypeId, Date dateDecision, String numeroArretePrefix, Long numeroArreteIndex, NumeroArreteDTO numeroArreteDTO, ContexteExecSignetPersoGWT contexteExecSignetPersoGWT) throws SeditGwtException {
        try {
            ucImprimeAG.createCourrierPromus4Tableau(mapper.cloneToModel(tableau, TableauAG.class), modeleTypeId, dateDecision, numeroArretePrefix, numeroArreteIndex, numeroArreteDTO, mapper.cloneToModel(contexteExecSignetPersoGWT, ContexteExecSignetPerso.class));
        } catch (BusinessException be) {
            throw new SeditGwtException(SeditGwtException.TYPE_BUSINESS, be);
        } catch (SeditException se) {
            throw new SeditGwtException(SeditGwtException.TYPE_TECHNICAL, se);
        } catch (Throwable t) {
            throw new SeditGwtException(SeditGwtException.TYPE_INCONNUE, t);
        }
    }

    @Override
    public List<CollectiviteNumerotationArreteParamDTO> getListCollectiviteArreteCourrierBatch(TableauAGGWT tableau) throws SeditGwtException {
        return SeditGwtTry.get(() -> this.ucImprimeAG.getListCollectiviteArreteCourrierBatch(mapper.cloneToModel(tableau, TableauAG.class)));
    }

    /**
     * Lancement KSL des éditions : liste des promus et tableaux d'avancements
     *
     * @param gwtTableauAVG
     * @param paramPresCAPId
     * @return
     * @throws SeditGwtException
     * @author aurore.maigron
     */
    @Override
    public String printCAPByTableau(Map<String, Serializable> params) throws SeditGwtException {
        try {
            return ucTableauAG.printCAPByTableau(params);
        } catch (BusinessException be) {
            throw new SeditGwtException(SeditGwtException.TYPE_BUSINESS, be);
        } catch (SeditException se) {
            throw new SeditGwtException(SeditGwtException.TYPE_TECHNICAL, se);
        } catch (Throwable t) {
            throw new SeditGwtException(SeditGwtException.TYPE_INCONNUE, t);
        }
    }

    /**
     * Lancement KSL des éditions : listes promus concours et liste promouvables concours
     *
     * @param typePresentation
     * @param strDateDeb
     * @param strDateFin
     * @param paramPresCAPId
     * @return
     * @throws SeditGwtException
     */
    @Override
    public String printPromotions(EnumTypePresentationGWT typePresentation, String strDateDeb, String strDateFin, Integer paramPresCAPId) throws SeditGwtException {
        try {

            return ucTableauAG.printPromotions(mapper.cloneToModel(typePresentation, EnumTypePresentation.class), strDateDeb, strDateFin, paramPresCAPId);
        } catch (BusinessException be) {
            throw new SeditGwtException(SeditGwtException.TYPE_BUSINESS, be);
        } catch (SeditException se) {
            throw new SeditGwtException(SeditGwtException.TYPE_TECHNICAL, se);
        } catch (Throwable t) {
            throw new SeditGwtException(SeditGwtException.TYPE_INCONNUE, t);
        }
    }

    /**
     * @see fr.bl.client.grh.ca.avg.remote.IGwtServiceCaAvg#findParamPresentationByTableau(TableauAGGWT)
     */
    @Override
    public List<ParamPresentationCAPDTOGWT> findParamPresentationByTableau(TableauAGGWT tableau, EnumTypePresentationGWT typePresentationGWT) throws SeditGwtException {
        return SeditGwtTry.get(() -> {
            if (tableau == null) return null;
            TableauAG serverTableau = mapper.cloneToModel(tableau, TableauAG.class);
            EnumTypePresentation typePresentation = mapper.cloneToModel(typePresentationGWT, EnumTypePresentation.class);
            List<ParamPresentationCAPDTO> serverResult = ucTableauAG.findParamPresentationByTableau(serverTableau, typePresentation);
            return mapper.cloneToGwt(serverResult, ParamPresentationCAPDTOGWT.class);
        });
    }

    /**
     * @param orga
     * @param typePresentation
     * @return
     * @throws SeditGwtException
     */
    @Override
    public List<ParamPresentationCAPDTOGWT> findParamPresentationByTypePresentation(OrganismeGWT orga, EnumTypePresentationGWT typePresentation) throws SeditGwtException {
        return SeditGwtTry.get(() -> {
            Organisme serverOrga = mapper.cloneToModel(orga, Organisme.class);
            EnumTypePresentation serverTypePres = mapper.cloneToModel(typePresentation, EnumTypePresentation.class);
            List<ParamPresentationCAPDTO> serverResult = ucTableauAG.findParamPresentationByTypePresentation(serverOrga, serverTypePres);
            return mapper.cloneToGwt(serverResult, ParamPresentationCAPDTOGWT.class);
        });
    }

    /**
     * Récupère en fonction de l'état du tableau le paramétrage de l'édition associé
     *
     * @param tableau
     * @param organisme
     * @return
     */
    @Override
    public ParamPresentationCAPDTOGWT findParamPresentationDefautOrganismeByTableau(TableauAGGWT tableau, EnumTypePresentationGWT typePresentationGWT) throws SeditGwtException {
        return SeditGwtTry.get(() -> {
            if (tableau == null) return null;
            TableauAG serverTableau = mapper.cloneToModel(tableau, TableauAG.class);
            EnumTypePresentation typePresentation = mapper.cloneToModel(typePresentationGWT, EnumTypePresentation.class);
            ParamPresentationCAPDTO serverResult = ucTableauAG.findParamPresentationDefautOrganismeByTableau(serverTableau, typePresentation);
            if (serverResult == null) return null;
            else return mapper.cloneToGwt(serverResult, ParamPresentationCAPDTOGWT.class);
        });
    }

    @Override
    public ParamPresentationCAPDTOGWT findParamPresentationDefautOrganisme(String organismeId, EnumTypePresentationGWT typePresentationGWT, EnumTypeTableauGWT typeTableauGWT) throws SeditGwtException {
        return SeditGwtTry.get(() -> {
            EnumTypePresentation typePresentation = null;
            if (typePresentationGWT != null) {
                typePresentation = mapper.cloneToModel(typePresentationGWT, EnumTypePresentation.class);
            }
            EnumTypeTableau typeTableau = null;
            if (typeTableauGWT != null) {
                typeTableau = mapper.cloneToModel(typeTableauGWT, EnumTypeTableau.class);
            }
            ParamPresentationCAPDTO serverResult = ucTableauAG.findParamPresentationDefautOrganisme(organismeId, typePresentation, typeTableau);
            if (serverResult == null) return null;
            else return mapper.cloneToGwt(serverResult, ParamPresentationCAPDTOGWT.class);
        });
    }

    /**
     * @see fr.bl.client.grh.ca.avg.remote.IGwtServiceCaAvg#calculRatiosEtQuotas(TableauAGGWT, GradeGWT, Boolean)
     */
    @Override
    public List<RatiosQuotasParTableauGWT> calculRatiosEtQuotas(TableauAGGWT tableauGWT, GradeGWT gradePropositionGWT, Boolean recalcul) throws SeditGwtException {
        try {
            TableauAG tableau = mapper.cloneToModel(tableauGWT, TableauAG.class);
            Grade grade = mapper.cloneToModel(gradePropositionGWT, Grade.class);
            tableau = ucTableauAG.loadCompleteTableauAGWithoutProposition(tableau.getId());
            List<RatiosQuotasParTableau> listratioquota = ucRatiosQuotasParTableau.calculRatiosEtQuotas(tableau, grade, recalcul);
            return mapper.cloneToGwt(listratioquota, RatiosQuotasParTableauGWT.class);
        } catch (BusinessException be) {
            if (SeditException.Type.MULTIPLE_EXCEPTION.equals(be.getType()))
                throw new SeditGwtException(be.getExceptionMap());
            else throw new SeditGwtException(SeditGwtException.TYPE_BUSINESS, be);
        } catch (SeditException se) {
            throw new SeditGwtException(SeditGwtException.TYPE_TECHNICAL, se);
        } catch (Throwable t) {
            throw new SeditGwtException(SeditGwtException.TYPE_INCONNUE, t);
        }
    }

    /**
     * @see fr.bl.client.grh.ca.avg.remote.IGwtServiceCaAvg#loadCompleteRatiosQuotasParTableauWithGradeById(Integer)
     */
    @Override
    public RatiosQuotasParTableauGWT loadCompleteRatiosQuotasParTableauWithGradeById(Integer ratiosQuotasParTableauId) throws SeditGwtException {
        try {
            RatiosQuotasParTableau ratioquota = ucRatiosQuotasParTableau.loadCompleteRatiosQuotasParTableauWithGradeById(ratiosQuotasParTableauId);
            return mapper.cloneToGwt(ratioquota, RatiosQuotasParTableauGWT.class);
        } catch (BusinessException be) {
            throw new SeditGwtException(SeditGwtException.TYPE_BUSINESS, be);
        } catch (SeditException se) {
            throw new SeditGwtException(SeditGwtException.TYPE_TECHNICAL, se);
        } catch (Throwable t) {
            throw new SeditGwtException(SeditGwtException.TYPE_INCONNUE, t);
        }
    }

    /**
     * @see fr.bl.client.grh.ca.avg.remote.IGwtServiceCaAvg#findListGroupsValidateurs(Map, TableauAGGWT, CollectiviteGWT, boolean, long, long)
     */
    @Override
    public List<GroupeTachesDTOGWT> findListGroupsValidateurs(Map<String, Integer> tri, TableauAGGWT tableauAG, CollectiviteGWT collectivite, boolean remplir, long firstLine, long limitLine) throws SeditGwtException {
        return SeditGwtTry.get(() -> {
            List<GroupeTachesDTO> result = ucSuiviAvisAG.findListGroupsValidateurs(tri, mapper.cloneToModel(tableauAG, TableauAG.class), (collectivite == null ? null : mapper.cloneToModel(collectivite, Collectivite.class)), remplir, firstLine, limitLine);
            return mapper.cloneToGwt(result, GroupeTachesDTOGWT.class);
        });
    }

    /**
     * @see fr.bl.client.grh.ca.avg.remote.IGwtServiceCaAvg#getMailInfo(Long)
     */
    @Override
    public InfoCourrielDTOGWT getMailInfo(Long taskId) throws SeditGwtException {
        return SeditGwtTry.get(() -> {
            InfoCourrielDTO result = ucSuiviAvisAG.getMailInfo(taskId);
            return mapper.cloneToGwt(result, InfoCourrielDTOGWT.class);
        });
    }

    /**
     * @see fr.bl.client.grh.ca.avg.remote.IGwtServiceCaAvg#isDemandesAvisAGDemarre(TableauAGGWT)
     */
    @Override
    public Boolean isDemandesAvisAGDemarre(TableauAGGWT tableau) throws SeditGwtException {
        return SeditGwtTry.get(() -> ucSuiviAvisAG.isWfAvisStarted(mapper.cloneToModel(tableau, TableauAG.class)));
    }

    /**
     * @see fr.bl.client.grh.ca.avg.remote.IGwtServiceCaAvg#demarrerDemandesAvisAG(UtilisateurSMRHGWT, TableauAGGWT)
     */
    @Override
    public void demarrerDemandesAvisAG(UtilisateurSMRHGWT utilisateurGWT, TableauAGGWT tableau) throws SeditGwtException {
        SeditGwtTry.run(() -> {
            UtilisateurSMRH utilisateur = mapper.cloneToModel(utilisateurGWT, UtilisateurSMRH.class);
            ucSuiviAvisAG.demarrerWfAvis(utilisateur, mapper.cloneToModel(tableau, TableauAG.class), null);
        });
    }

    /**
     * @see fr.bl.client.grh.ca.avg.remote.IGwtServiceCaAvg#findCollectivitesInTableauAG(TableauAGGWT)
     */
    @Override
    public Set<CollectiviteGWT> findCollectivitesInTableauAG(TableauAGGWT tableau) throws SeditGwtException {
        return SeditGwtTry.get(() -> {
            Set<Collectivite> serverResult = ucSuiviAvisAG.findCollectivitesInTableau(mapper.cloneToModel(tableau, TableauAG.class));
            return mapper.cloneToGwt(serverResult, CollectiviteGWT.class);
        });
    }

    /**
     * @see fr.bl.client.grh.ca.avg.remote.IGwtServiceCaAvg#checkAndSaveAndClotureTask(TableauAGGWT, TacheWfEcheancierDTOGWT, String)
     */
    @Override
    public void checkAndSaveAndClotureTask(TableauAGGWT tableauAG, TacheWfEcheancierDTOGWT task, String userId) throws SeditGwtException {
        SeditGwtTry.run(() -> {
            TableauAG serverTableau = mapper.cloneToModel(tableauAG, TableauAG.class);
            TacheWfEcheancierDTO tacheWfEcheancierDTO = mapper.cloneToModel(task, TacheWfEcheancierDTO.class);
            ucSuiviAvisAG.checkAndSaveAndClotureTask(serverTableau, tacheWfEcheancierDTO, userId);
        });
    }

    /**
     * @see fr.bl.client.grh.ca.avg.remote.IGwtServiceCaAvg#endSaisieAG(UtilisateurSMRHGWT, Long)
     */
    @Override
    public void endSaisieAG(UtilisateurSMRHGWT utilisateurGWT, Long taskId) throws SeditGwtException {
        SeditGwtTry.run(() -> {
            UtilisateurSMRH utilisateur = mapper.cloneToModel(utilisateurGWT, UtilisateurSMRH.class);
            ucSaisieAvisAG.endSaisieAG(utilisateur, taskId);
        });
    }

    /**
     * @see fr.bl.client.grh.ca.avg.remote.IGwtServiceCaAvg#getSaisieInfoAGByTaskId(UtilisateurSMRHGWT, Long)
     */
    @Override
    public Map<String, Object> getSaisieInfoAGByTaskId(UtilisateurSMRHGWT utilisateurGWT, Long taskId) throws SeditGwtException {
        return SeditGwtTry.get(() -> {
            UtilisateurSMRH utilisateur = mapper.cloneToModel(utilisateurGWT, UtilisateurSMRH.class);
            Map<String, Object> result = ucSaisieAvisAG.getSaisieInfoAGByTaskId(utilisateur, taskId, null);
            return translateToGwt(result);
        });
    }

    /**
     * @see fr.bl.client.grh.ca.avg.remote.IGwtServiceCaAvg#getSaisieInfoAGByTaskName(UtilisateurSMRHGWT, String)
     */
    @Override
    public Map<String, Object> getSaisieInfoAGByTaskName(UtilisateurSMRHGWT utilisateurGWT, String taskName) throws SeditGwtException {
        return SeditGwtTry.get(() -> {
            UtilisateurSMRH utilisateur = mapper.cloneToModel(utilisateurGWT, UtilisateurSMRH.class);
            Map<String, Object> result = ucSaisieAvisAG.getSaisieInfoAGByTaskName(utilisateur, taskName, null);
            return translateToGwt(result);
        });
    }


    private Map<String, Object> translateToGwt(Map<String, Object> map) {
        Map<String, Object> result = new HashMap<String, Object>(map);
        TableauAG tableauAG = (TableauAG) result.get("tableauAG");
        if (tableauAG != null) {
            result.put("tableauAG", mapper.cloneToGwt(tableauAG, TableauAGGWT.class));
        }
        Collectivite collectivite = (Collectivite) result.get("collectivite");
        if (collectivite != null) {
            result.put("collectivite", mapper.cloneToGwt(collectivite, CollectiviteGWT.class));
        }
        return result;
    }

    /**
     * @see fr.bl.client.grh.ca.avg.remote.IGwtServiceCaAvg#findListServicesInPropositionAG(Map)
     */
    @Override
    public List<ServiceDTOGWT> findListServicesInPropositionAG(Map<String, Object> criteria) throws SeditGwtException {
        return SeditGwtTry.get(() -> {
            translateCriteria(criteria);
            List<ServiceDTO> result = ucSaisieAvisAG.findListServicesInPropositionAG(criteria);
            if (result == null) return null;
            else return mapper.cloneToGwt(result, ServiceDTOGWT.class);
        });
    }

    /**
     * @see fr.bl.client.grh.ca.avg.remote.IGwtServiceCaAvg#isPropositionAgHasConcoursAgent(java.lang.String)
     */
    @Override
    public Boolean isPropositionAgHasConcoursAgent(String idConcoursAgent) {
        return ucPropositionAG.isPropositionAgHasConcoursAgent(idConcoursAgent);
    }

    /**
     * @see fr.bl.client.grh.ca.avg.remote.IGwtServiceCaAvg#findListTableauAGDTOByCodeOrLibelle(java.lang.String, fr.bl.client.grh.coeur.cs.model.parametrage.OrganismeGWT)
     */
    @Override
    public List<TableauAGDTOGWT> findListTableauAGDTOByCodeOrLibelle(String codeOrLibelle, OrganismeGWT organisme, Map<String, Object> criteria) throws SeditGwtException {
        return SeditGwtTry.get(() -> {
            Organisme orga = null;
            if (organisme != null) {
                orga = mapper.cloneToModel(organisme, Organisme.class);
            }
            EnumPhaseTableauAGGWT enumPhaseTableauAGGWT = (EnumPhaseTableauAGGWT) criteria.get("etatTableau<");
            if (enumPhaseTableauAGGWT != null) {
                criteria.put("etatTableau<", mapper.cloneToModel(enumPhaseTableauAGGWT, EnumPhaseTableauAG.class));
            }
            List<TableauAGDTO> listeTableauAGDTO = ucTableauAG.findListTableauAGDTOByCodeOrLibelle(codeOrLibelle, orga, criteria);
            if (listeTableauAGDTO == null || listeTableauAGDTO.isEmpty()) return null;
            return mapper.cloneToGwt(listeTableauAGDTO, TableauAGDTOGWT.class);
        });
    }

    /**
     * @see fr.bl.client.grh.ca.avg.remote.IGwtServiceCaAvg#countPropositionsPromouvableByTableauAG(java.lang.String)
     */
    @Override
    public Long countPropositionsPromouvableByTableauAG(String code) throws SeditGwtException {
        return SeditGwtTry.get(() -> ucPropositionAG.countPropositionsPromouvableByTableauAG(code));
    }

    /**
     * supprime la proposition ag id
     *
     * @param id
     */
    @Override
    public void deletePropositionAGById(Integer id) throws SeditGwtException {
        SeditGwtTry.run(() -> ucPropositionAG.deletePropositionById(id));

    }

    @Override
    public Map<String, Date> findDatesForControleInjection(final List<String> codesCollectivite) throws SeditGwtException {
        return SeditGwtTry.get(() -> ucPropositionAG.findDatesForControleInjection(codesCollectivite));
    }

    /**
     * @throws SeditGwtException
     * @see {@link IGwtServiceCaAvg#changePromuPropositionAG(Integer, boolean)}
     */
    @Override
    public void changePromuSPVPropositionAG(Integer propositionId, boolean promu) throws SeditGwtException {
        try {
            ucElaborationCAP.changePromuSPVPropositionAG(propositionId, promu);
        } catch (BusinessException e) {
            if (SeditException.Type.MULTIPLE_EXCEPTION.equals(e.getType()))
                throw new SeditGwtException(e.getExceptionMap());
            else throw new SeditGwtException(SeditGwtException.TYPE_BUSINESS, e);
        } catch (TechnicalException e) {
            if (SeditException.Type.MULTIPLE_EXCEPTION.equals(e.getType()))
                throw new SeditGwtException(e.getExceptionMap());
            else throw new SeditGwtException(SeditGwtException.TYPE_TECHNICAL, e);
        } catch (Exception e) {
            throw new SeditGwtException(SeditGwtException.TYPE_INCONNUE, e);
        }
    }

    @Override
    public List<CadreEmploiDTOGWT> findListCadreEmploiDTO(String idCadreStatutaire, Date date) throws SeditGwtException {
        return SeditGwtTry.get(() -> {
            List<CadreEmploiDTO> retour = ucCrudCadreEmploi.findListCadreEmploiDTO(idCadreStatutaire, date);
            return mapper.cloneToGwt(retour, CadreEmploiDTOGWT.class);
        });
    }

    /**
     * {@inheritDoc}
     *
     * @see fr.bl.client.grh.ca.avg.remote.IGwtServiceCaAvg#findCollectiviteByPropositionIds(java.util.List)
     */
    @Override
    public List<CollectiviteNumerotationArreteParamDTO> findCollectiviteByPropositionIds(final List<Integer> propositionIds) throws SeditGwtException {
        try {
            return this.ucImprimeAG.findCollectiviteByPropositionIds(propositionIds);
        } catch (final SeditException se) {
            if (SeditException.Type.MULTIPLE_EXCEPTION.equals(se.getType())) {
                throw new SeditGwtException(se.getExceptionMap());
            } else {
                throw new SeditGwtException(se instanceof BusinessException ? SeditGwtException.TYPE_BUSINESS : SeditGwtException.TYPE_TECHNICAL, se);
            }
        } catch (final Throwable t) {
            throw new SeditGwtException(SeditGwtException.TYPE_INCONNUE, t);
        }
    }

    /**
     * {@inheritDoc}
     *
     * @see fr.bl.client.grh.ca.avg.remote.IGwtServiceCaAvg#findCollectiviteByPropositionIds(fr.bl.client.grh.coeur.ca.avg.model.TableauAGGWT)
     */
    @Override
    public List<CollectiviteNumerotationArreteParamDTO> findCollectiviteByPropositionIds(final TableauAGGWT tableau) throws SeditGwtException {
        try {
            return this.ucImprimeAG.findCollectiviteByPropositionIds(this.mapper.cloneToModel(tableau, TableauAG.class));
        } catch (final SeditException se) {
            if (SeditException.Type.MULTIPLE_EXCEPTION.equals(se.getType())) {
                throw new SeditGwtException(se.getExceptionMap());
            } else {
                throw new SeditGwtException(se instanceof BusinessException ? SeditGwtException.TYPE_BUSINESS : SeditGwtException.TYPE_TECHNICAL, se);
            }
        } catch (final Throwable t) {
            throw new SeditGwtException(SeditGwtException.TYPE_INCONNUE, t);
        }
    }

    /**
     * {@inheritDoc}
     *
     * @see fr.bl.client.grh.ca.avg.remote.IGwtServiceCaAvg#findCollectiviteByPropositionIds(java.util.Map)
     */
    @Override
    public List<CollectiviteNumerotationArreteParamDTO> findCollectiviteByPropositionIds(final Map<String, Object> criteria) throws SeditGwtException {
        try {
            this.translateCriteria(criteria);
            return this.ucImprimeAG.findCollectiviteByPropositionIds(criteria);
        } catch (final SeditException se) {
            if (SeditException.Type.MULTIPLE_EXCEPTION.equals(se.getType())) {
                throw new SeditGwtException(se.getExceptionMap());
            } else {
                throw new SeditGwtException(se instanceof BusinessException ? SeditGwtException.TYPE_BUSINESS : SeditGwtException.TYPE_TECHNICAL, se);
            }
        } catch (final Throwable t) {
            throw new SeditGwtException(SeditGwtException.TYPE_INCONNUE, t);
        }
    }

    @Override
    public CalculatriceCarriereDTO calculDureeEntreDate(Date dateDebut, Date dateFin) throws SeditGwtException {
        try {
            return this.ucCalculatrice.calculDureeEntreDate(dateDebut, dateFin);
        } catch (final SeditException se) {
            if (SeditException.Type.MULTIPLE_EXCEPTION.equals(se.getType())) {
                throw new SeditGwtException(se.getExceptionMap());
            } else {
                throw new SeditGwtException(se instanceof BusinessException ? SeditGwtException.TYPE_BUSINESS : SeditGwtException.TYPE_TECHNICAL, se);
            }
        } catch (final Throwable t) {
            throw new SeditGwtException(SeditGwtException.TYPE_INCONNUE, t);
        }
    }

    @Override
    public CalculatriceCarriereDTO calculDureeAjouterRetirer(Date dateToCalcul, HashMap<String, Integer> paramCalcul) throws SeditGwtException {
        try {
            return this.ucCalculatrice.calculDureeAjouterRetirer(dateToCalcul, paramCalcul);
        } catch (final SeditException se) {
            if (SeditException.Type.MULTIPLE_EXCEPTION.equals(se.getType())) {
                throw new SeditGwtException(se.getExceptionMap());
            } else {
                throw new SeditGwtException(se instanceof BusinessException ? SeditGwtException.TYPE_BUSINESS : SeditGwtException.TYPE_TECHNICAL, se);
            }
        } catch (final Throwable t) {
            throw new SeditGwtException(SeditGwtException.TYPE_INCONNUE, t);
        }
    }


    @Override
    public CalculatriceCarriereDTO cumulerDuree(CalculatriceCarriereDTO duree1, CalculatriceCarriereDTO duree2, EnumCalculatriceOperation operation) throws SeditGwtException {
        try {
            return ucCalculatrice.cumulerDuree(duree1, duree2, operation);
        } catch (final SeditException se) {
            if (SeditException.Type.MULTIPLE_EXCEPTION.equals(se.getType())) {
                throw new SeditGwtException(se.getExceptionMap());
            } else {
                throw new SeditGwtException(se instanceof BusinessException ? SeditGwtException.TYPE_BUSINESS : SeditGwtException.TYPE_TECHNICAL, se);
            }
        } catch (final Throwable t) {
            throw new SeditGwtException(SeditGwtException.TYPE_INCONNUE, t);
        }
    }

    @Override
    public AgentGWT loadAgentWithCarriereAndFicheGradeEmploi(String agentId) throws SeditGwtException {
        return SeditGwtTry.get(() -> this.mapper.cloneToGwt(this.ucVerificationPropositionAGCAP.loadAgentWithCarriereAndFicheGradeEmploi(agentId), AgentGWT.class));
    }
    

    @Override
    public Boolean importExcelPropositionAG(Map<String,Object> criteres) throws SeditGwtException {
    	
    	return SeditGwtTry.get(() -> {
			if (criteres.containsKey("tableauAG")) {
				Object objTableauAGId = criteres.get("tableauAG");
				Integer tableauAGId;
				if (objTableauAGId instanceof Integer) {
					tableauAGId = (Integer) criteres.get("tableauAG");
				} else {
					tableauAGId = Integer.valueOf((String) criteres.get("tableauAG"));
				}
				if (tableauAGId != null) {
					criteres.put("tableauAG", tableauAGId);
				}
			}

            Boolean retour = ucVerificationPropositionAGCAP.importExcelPropositionAG(criteres);

            return mapper.cloneToGwt(retour, Boolean.class);
        });

    }

}

package fr.bl.client.grh.ca.avg.cp.suiviAvis;

import java.io.File;
import java.io.FileInputStream;
import java.util.HashMap;
import java.util.Map;

import org.apache.poi.ss.usermodel.*;

import org.apache.poi.ss.usermodel.Workbook;

import com.google.gwt.event.dom.client.ChangeEvent;
import com.google.gwt.event.dom.client.ChangeHandler;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.user.client.ui.FileUpload;
import com.google.gwt.user.client.ui.FormPanel.SubmitCompleteEvent;
import com.google.gwt.user.client.ui.FormPanel.SubmitCompleteHandler;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.HasHorizontalAlignment;

import fr.bl.client.core.common.SecureAsyncCallback;
import fr.bl.client.core.common.UtilsJS;
import fr.bl.client.core.gab.Workspace;
import fr.bl.client.core.phasecontrol.impl.AbstractSimplePageMetier;
import fr.bl.client.core.refui.base.components.BLFieldSetPanel;
import fr.bl.client.core.refui.base.components.BLFileUpload;
import fr.bl.client.core.refui.base.components.BLFlexTable;
import fr.bl.client.core.refui.base.components.BLGroupBoxPanel;
import fr.bl.client.core.refui.base.components.BLHorizontalPanel;
import fr.bl.client.core.refui.base.components.BLLabel;
import fr.bl.client.core.refui.base.components.BLLinkButton;
import fr.bl.client.core.refui.base.components.BLLinkLabel;
import fr.bl.client.core.refui.base.components.BLVerticalPanel;
import fr.bl.client.core.refui.base.event.BLEventPopup;
import fr.bl.client.core.refui.components.ComponentUtils;
import fr.bl.client.grh.ca.avg.cp.BLPanelCriteresTableauAG;
import fr.bl.client.grh.ca.avg.cp.TableauAGTableList;
import fr.bl.client.grh.ca.avg.cp.elaboration.ListModelElaboration;
import fr.bl.client.grh.ca.avg.cp.proposition.BLPanelPropositionAGAgent;
import fr.bl.client.grh.ca.avg.remote.IGwtServiceCaAvg;
import fr.bl.client.grh.coeur.ca.avg.model.TableauAGGWT;
import fr.bl.client.grh.coeur.ca.avg.model.enums.EnumPhaseTableauAGGWT;
import fr.bl.client.grh.coeur.composants.listBox.ListBoxPlanPaie;
import fr.bl.client.grh.coeur.cs.model.parametrage.OrganismeGWT;
import fr.bl.client.grh.common.security.model.UserDTOGRHGWT;
import fr.bl.client.grh.n4ds.remote.IGwtServiceN4DS;
import fr.sedit.grh.coeur.cs.model.parametrage.Organisme;
import fr.sedit.grh.coeur.interfaces.ExcelFileUtils;

/**
 *	@author magali.duretti
 *	Date création : 14 févr. 08
 */
public class PageMetierSuiviAvisPropPromouvableAG extends AbstractSimplePageMetier{
	
	TableauAGGWT _tableau;
	
	private BLVerticalPanel _vpMain;
	BLHorizontalPanel _vpQSAndProgressionBar;
	BLPanelCriteresTableauAG _fspPropositions;
	BLPanelPropositionAGAgent _fspPropositionAgent;
	BLFieldSetPanel _fspPanelImportPromus; 
	TableSuiviAvisPropositionPromouvableAG _tablePropositionPromouvable=null;
   
	private final BLLinkButton btLaunchImportPromus = new BLLinkButton("Importer", BLLinkButton.NIVEAU1);
	private MyBLFileUpload fileUploadParcourirExcelPromus;
	Map<String, Object> _criteria; 
	
	  private class MyBLFileUpload extends BLFileUpload {
	        public MyBLFileUpload(final String folderName) {
	            super(folderName);
	        }
	        private void myInitPopupFileUpload(final String folderName) {
	            FileUpload fileUpload = this.getFileUpload();
	            fileUpload.addChangeHandler(new ChangeHandler() {				
					@Override
					public void onChange(ChangeEvent event) {
	                    btLaunchImportPromus.setEnabled(!fileUpload.getFilename().isEmpty());						
					}
	            });
	            
	        }
	    }
	public PageMetierSuiviAvisPropPromouvableAG( ) {
		super("Listes des promouvables");
	}
	
	@Override
	public void buildPageUi(Object object) {
		if (object instanceof TableauAGGWT) {
			_tableau = (TableauAGGWT) object;
		} else {
			return;
		}
	
		_fspPropositionAgent = new BLPanelPropositionAGAgent(this._tableau);
		_fspPropositionAgent.setWidth("650px");
		_fspPropositionAgent.setVisibleCreateAction(false);

		//***********************************************Table des propositions ********************************/
		if(EnumPhaseTableauAGGWT.SUIVI_AVIS.equals(_tableau.getEtatTableau())){
			this._tablePropositionPromouvable =  new TableSuiviAvisPropositionPromouvableAG(this._tableau,new ListModelSuiviAvis(_tableau, this),new String[]{"36%","10%","21%","21%","12%"});	
            this._tablePropositionPromouvable.setVisibleExclureAction(true);
        }
		else if(EnumPhaseTableauAGGWT.ATTENTE_CAP.equals(_tableau.getEtatTableau())){
			this._tablePropositionPromouvable =  new TableSuiviAvisPropositionPromouvableAG(this._tableau,new ListModelSuiviAvis(_tableau, this),new String[]{"36%","10%","21%","21%","12%"});	
            this._tablePropositionPromouvable.setVisibleExclureAction(true);
        }
		else if(EnumPhaseTableauAGGWT.ELABORATION_TABLEAU.equals(_tableau.getEtatTableau())){
			this._tablePropositionPromouvable =  new TableSuiviAvisPropositionPromouvableAG(this._tableau, new ListModelElaboration(_tableau, this),new String[]{"5%","34%","12%","12%","8%","19%","10%"});	
			//this._tablePropositionPromouvable.setVisibleEditerArreteAction(true);
           // this._tablePropositionPromouvable.setVisibleExclureAction(false);
		}
		_fspPropositions = new BLPanelCriteresTableauAG(this._tableau,this._tablePropositionPromouvable,"Propositions",this._tablePropositionPromouvable.getCriteria(),true);
		
		_fspPanelImportPromus = getPanelImportPromus();
		_fspPanelImportPromus.setWidth("100%");
		
		final BLFlexTable ftCritereAction = new BLFlexTable();
		ftCritereAction.setHeight("90px");
		ftCritereAction.setWidth("100%");
		ftCritereAction.setWidget(0, 0,_fspPropositionAgent, HasHorizontalAlignment.ALIGN_LEFT);
		ftCritereAction.setWidget(0, 1,new HTML("&nbsp;"), HasHorizontalAlignment.ALIGN_LEFT);
		ftCritereAction.setWidget(0, 2,_fspPanelImportPromus, HasHorizontalAlignment.ALIGN_LEFT);
		ftCritereAction.setWidget(0, 3,new HTML("&nbsp;"), HasHorizontalAlignment.ALIGN_LEFT);
		
		ftCritereAction.getFlexCellFormatter().setWidth(0, 0, "25%");
		ftCritereAction.getFlexCellFormatter().setWidth(0, 1, "3%");
		ftCritereAction.getFlexCellFormatter().setWidth(0, 2, "32%");
		ftCritereAction.getFlexCellFormatter().setWidth(0, 3, "40%");
		
		_vpQSAndProgressionBar = new  BLHorizontalPanel();
		_vpQSAndProgressionBar.add(ftCritereAction);	
		_vpQSAndProgressionBar.setWidth("100%");
		
		_vpMain= new BLVerticalPanel();
		_vpMain.setWidth("100%");
		_vpMain.add(_vpQSAndProgressionBar);
		_vpMain.add(_fspPropositions);
		
		setContent(_vpMain);
		setBuild(true);
	}
	
	@Override
	public String getElementIdentifier() {
	    return "PM_SUIVIS_AVIS_PROP_PROMOUVABLE_AG";
	}
	
	public void setVersionTableau(Integer version){
		if(_tableau!=null)this._tableau.setVersion(version);
	}
	public TableauAGTableList getTablePropositionPromouvable() {
		return _tablePropositionPromouvable;
	}
	
	private BLFieldSetPanel getPanelImportPromus() {
		this.setWidth("100%");
		BLFieldSetPanel fspMain = new BLFieldSetPanel("Importer les promus");
		final BLFlexTable ftMain = new BLFlexTable();
		fspMain.setWidget(ftMain);
		fspMain.setWidth("100%");
		ftMain.setWidth("100%");
		_criteria  = new HashMap<String, Object>();
		_criteria.put("tableauAG", _tableau.getId());
		_criteria.put("organisme", (String)((UserDTOGRHGWT) Workspace.getCurrentUser()).getContext().getOrganisme().getId().toString());
		btLaunchImportPromus.setEnabled(false);
		btLaunchImportPromus.addClickHandler(new ClickHandler() {
			@Override
			public void onClick(ClickEvent event) {
				boolean fileImportOK = false; 
				
				if (fileUploadParcourirExcelPromus.getFileName() == null || fileUploadParcourirExcelPromus.getFileName().isEmpty()) {
					BLEventPopup.displayError("Le fichier est absent ou n'est pas au bon format");
				} else {
					fileImportOK = true;
				}
				if (fileImportOK) {
					fireBeginAttente("Import des promus en cours...");
					btLaunchImportPromus.setEnabled(false);
					fileUploadParcourirExcelPromus.uploadFile();

				}

			}
		});

		ftMain.setWidget(0, 0, ComponentUtils.getLibelleItem("Fichier à importer", true), HasHorizontalAlignment.ALIGN_LEFT);
	
		IGwtServiceN4DS.Util.getInstance().getTempFilePathForImportExport(new SecureAsyncCallback<String>() {
			@Override
			public void onSuccess(final String result) {
				fileUploadParcourirExcelPromus = new MyBLFileUpload(result);
				fileUploadParcourirExcelPromus.setEnableFileFilter(true);
				fileUploadParcourirExcelPromus.addExtension("xls");
				
				fileUploadParcourirExcelPromus.addSubmitCompleteHandler(new SubmitCompleteHandler() {
					@Override
					public void onSubmitComplete(SubmitCompleteEvent event) {
						submit(event);
					}
				});
				fileUploadParcourirExcelPromus.setWidth("80%");
				fileUploadParcourirExcelPromus.myInitPopupFileUpload(result);
				if (result != null && result.endsWith(".xls")) {
					btLaunchImportPromus.setEnabled(true);
				}
				ftMain.setWidget(0, 1, fileUploadParcourirExcelPromus, HasHorizontalAlignment.ALIGN_LEFT);
				ftMain.setWidth("100%");
			}
		});
		ftMain.setWidget(0, 2, btLaunchImportPromus, HasHorizontalAlignment.ALIGN_RIGHT);
		return fspMain;
	}

	private void submit(SubmitCompleteEvent event) {
		String filePathServer = event.getResults();
		if (filePathServer != null) {
			filePathServer = filePathServer.replaceAll("<[^>]*>", "");
			if (filePathServer.toLowerCase().indexOf(".xls") != -1) {
				_criteria.put("filePathServer", filePathServer);
				btLaunchImportPromus.setEnabled(true);
				IGwtServiceCaAvg.Util.getInstance().importExcelPropositionAG(_criteria,
						new SecureAsyncCallback<Boolean>() {
							@Override
							public void onSuccess(Boolean b) {
								fireEndAttente();
								if(b) {
									BLEventPopup.displayInfo("L'import des données est terminé.");									
								}else {
									BLEventPopup.displayError("Fichier non conforme");
								}
								_tablePropositionPromouvable.loadFirstPageData();
							}

							@Override
							public void onFailure(Throwable caught) {
								super.onFailure(caught);
								fireEndAttente();
								BLEventPopup.displayError(caught.getMessage());
							}
						});
			} else {
				btLaunchImportPromus.setEnabled(true);
				BLEventPopup.displayError("Fichier import non conforme (XLS attendu)");
				fireEndAttente();

			}
		}
	}
	
	
}



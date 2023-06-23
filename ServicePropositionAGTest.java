package fr.sedit.grh.coeur.ca.avg.services.impl;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;

import fr.sedit.grh.ca.avg.model.dto.PropositionAGAExporterDTO;
import fr.sedit.grh.coeur.ca.avg.dao.IDaoTableauAG;
import fr.sedit.grh.coeur.ca.avg.model.PropositionAG;
import fr.sedit.grh.coeur.ca.avg.model.TableauAG;
import fr.sedit.grh.coeur.ca.avg.services.IServicePropositionAG;
import fr.sedit.grh.coeur.cs.dao.IDaoOrganisme;
import fr.sedit.notest.AbstractTestSpring;

public class ServicePropositionAGTest extends AbstractTestSpring {
    @Autowired
    @Qualifier("servicePropositionAG")
    private IServicePropositionAG servicePropositionAG;

    @Autowired 
    @Qualifier("daoOrganisme")
    private IDaoOrganisme daoOrganisme;

    @Autowired 
    @Qualifier("daoTableauAG")
    private IDaoTableauAG daoTableauAG;

    @Test(timeout = AbstractTestSpring.TIME_OUT_DEFAULT)
    public void testExportPropositionAGForExcel(){
        // Organisme base JUnit (REC_RH_JUNIT@RECRH19)
        //Organisme o = daoOrganisme.findOrganismeAndCadreStatutaireById("98765432100000539023000        ");

        // TableauAG Id = 1400970, code = 2019GRD, libelle = Avancement de grade 2019
        TableauAG tableauAG = daoTableauAG.loadTableauAG(1400970);

        Map<String, Object> criteres = new HashMap<String, Object>();
        criteres.put("tableauAGId", tableauAG.getId());
        criteres.put("tableauAG", tableauAG);
        criteres.put("nonPromouvable", Boolean.TRUE);
        criteres.put("promuExport", Boolean.TRUE);
        criteres.put("promouvable", Boolean.TRUE);

        List<PropositionAGAExporterDTO> lstPropositionAGAExporterDTO = servicePropositionAG.exportPropositionAGForExcel(criteres);

        assertNotNull(lstPropositionAGAExporterDTO);
        assertTrue(lstPropositionAGAExporterDTO.size() > 0);
        PropositionAGAExporterDTO propAG = lstPropositionAGAExporterDTO.get(0);
        // ESRH-6729
        assertNotNull(propAG.getCarriereId());
        assertNull(propAG.getLibelleCampagneEvaluation());
        assertNull(propAG.getDateEntretien());
        assertNull(propAG.getValeurProfessionnelle());
        assertNull(propAG.getPoidsValeurProfessionnelle());
        assertNull(propAG.getAvisEvaluateur());
        assertNull(propAG.getCommentaireEvaluateur());
    }
    
    @Test(timeout = AbstractTestSpring.TIME_OUT_DEFAULT)
    public void testImportPropositionAGForExcel(){

    	  Workbook wb =  new XSSFWorkbook(); 
          Sheet sheet =  wb.createSheet("LDG");
          Row row1 = sheet.createRow(0);
          Row row2 = sheet.createRow(1);
          
          Cell cell1 = row1.createCell(0);
          cell1.setCellValue("Décision promu");
          Cell cell2 = row1.createCell(1);
          cell2.setCellValue("Classement");
          Cell cell3 = row1.createCell(7);
          cell3.setCellValue("MatriculeAgent");
          Cell cell4 = row1.createCell(48);
          cell4.setCellValue("TypeAvancement");
          
          
          Cell cella = row2.createCell(0);
          cella.setCellValue("Oui");
          Cell cellb = row2.createCell(1);
          cellb.setCellValue("class123");
          Cell cellc = row2.createCell(7);
          cellc.setCellValue("00052");
          Cell celld = row2.createCell(48);
          celld.setCellValue("Avancement de grade");
          
          String filePath = "Workbook.xlsx";
          File f = null ;
          try (FileOutputStream outputStream = new FileOutputStream(filePath)) {
              wb.write(outputStream);
              f = new File(filePath);
              filePath = f.getAbsolutePath();
          } catch (IOException e) {
              e.printStackTrace();
          }
          
//          f.delete();

        try {
            // Call the method to test
            List<PropositionAG> result =null ;
//            result = servicePropositionAG.importPropositionAGForExcel(filePath);
            
            // Verify the results
            assertNotNull(result);
            assertFalse(result.isEmpty());

            // If you know the expected results, you can do more detailed verifications
            // For example, you can verify the number of items imported, the values of the properties, etc.

        } catch (Exception e) {
            e.printStackTrace();

        }
    }
}

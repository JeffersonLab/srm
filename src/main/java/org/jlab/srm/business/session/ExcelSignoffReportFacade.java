package org.jlab.srm.business.session;

import jakarta.ejb.EJB;
import jakarta.ejb.Stateless;
import java.io.IOException;
import java.io.OutputStream;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.jlab.smoothness.business.util.TimeUtil;
import org.jlab.srm.persistence.model.SignoffReportRecord;

/**
 * @author ryans
 */
@Stateless
public class ExcelSignoffReportFacade {

  @EJB CategoryFacade categoryFacade;

  private Sheet sheet = null;
  private int rownum = 0;

  public void export(
      OutputStream out, List<SignoffReportRecord> recordList, String selectionMessage)
      throws IOException {
    Date now = new Date();
    SimpleDateFormat dateFormat = new SimpleDateFormat(TimeUtil.getFriendlyDateTimePattern());
    String formattedGgeneratedDate = dateFormat.format(now);

    Workbook wb = new XSSFWorkbook();
    Sheet sheet1 = wb.createSheet("Signoff Report (Generated " + formattedGgeneratedDate + ")");

    int rownum = 0;
    Row row0 = sheet1.createRow(rownum++);
    row0.createCell(0).setCellValue(selectionMessage);

    Row row1 = sheet1.createRow(rownum++);
    row1.createCell(0).setCellValue("COMPONENT");
    row1.createCell(1).setCellValue("GROUP");
    row1.createCell(2).setCellValue("MODIFIED DATE");
    row1.createCell(3).setCellValue("MODIFIED BY");
    row1.createCell(4).setCellValue("STATUS");
    row1.createCell(5).setCellValue("COMMENTS");

    CreationHelper createHelper = wb.getCreationHelper();
    CellStyle numberStyle = wb.createCellStyle();
    numberStyle.setDataFormat(createHelper.createDataFormat().getFormat("##0.###"));
    CellStyle dateStyle = wb.createCellStyle();
    dateStyle.setDataFormat(
        createHelper.createDataFormat().getFormat(TimeUtil.getFriendlyDateTimePattern()));

    Cell c;

    for (SignoffReportRecord record : recordList) {
      Row row = sheet1.createRow(rownum++);

      row.createCell(0).setCellValue(record.getComponentName());
      row.createCell(1).setCellValue(record.getGroupName());
      c = row.createCell(2);
      if (record.getModifiedDate() == null) {
        c.setCellValue("");
      } else {
        c.setCellStyle(dateStyle);
        c.setCellValue(record.getModifiedDate());
      }
      c = row.createCell(3);
      if (record.getModifiedBy() == null) {
        c.setCellValue("");
      } else {
        c.setCellValue(record.getModifiedBy());
      }
      row.createCell(4).setCellValue(record.getStatus().getName());
      c = row.createCell(5);
      if (record.getComments() == null) {
        c.setCellValue("");
      } else {
        c.setCellValue(record.getComments());
      }
    }

    /*sheet1.autoSizeColumn(0);*/
    sheet1.autoSizeColumn(1);
    sheet1.autoSizeColumn(2);
    sheet1.autoSizeColumn(3);
    sheet1.autoSizeColumn(4);
    sheet1.autoSizeColumn(5);

    wb.write(out);
  }
}

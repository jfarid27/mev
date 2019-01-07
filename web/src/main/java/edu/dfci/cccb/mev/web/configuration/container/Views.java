/**
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package edu.dfci.cccb.mev.web.configuration.container;

import org.springframework.context.annotation.Bean;
import org.springframework.web.servlet.view.freemarker.FreeMarkerView;

import edu.dfci.cccb.mev.dataset.client.support.freemarker.FreeMarkerViewBuilder;

/**
 * @author levk
 * 
 */
public class Views {

  @Bean
  public FreeMarkerView home (FreeMarkerViewBuilder builder) {
    return builder.setUrl ("/edu/dfci/cccb/mev/web/views/home.ftl").build ();
  }
  
  @Bean
  public FreeMarkerView index (FreeMarkerViewBuilder builder) {
    return builder.setUrl ("/edu/dfci/cccb/mev/web/ui/stealmain.html").build ();
  }

  @Bean
  public FreeMarkerView api (FreeMarkerViewBuilder builder) {
    return builder.setUrl ("/edu/dfci/cccb/mev/web/views/api.ftl").build ();
  }
  
  // Elements
  
  @Bean (name = "elements/heatmapSettingsModalBody")
  public FreeMarkerView elementHeatmapSettingsModalBody (FreeMarkerViewBuilder builder) {
    return builder.setUrl ("/edu/dfci/cccb/mev/web/views/elements/heatmapSettingsModalBody.ftl").build ();
  }

  @Bean (name = "elements/anovaModalBody")
  public FreeMarkerView elementAnovaModalBody (FreeMarkerViewBuilder builder) {
    return builder.setUrl ("/edu/dfci/cccb/mev/web/views/elements/anovaModalBody.ftl").build ();
  }
  
  @Bean (name = "elements/tTestModalBody")
  public FreeMarkerView elementTTestModalBody (FreeMarkerViewBuilder builder) {
    return builder.setUrl ("/edu/dfci/cccb/mev/web/views/elements/tTestModalBody.ftl").build ();
  }
  
  @Bean (name = "elements/heatmapNavigation")
  public FreeMarkerView elementHeatmapNavigation (FreeMarkerViewBuilder builder) {
    return builder.setUrl ("/edu/dfci/cccb/mev/web/views/elements/heatmapNavigation.ftl").build ();
  }

  @Bean (name = "elements/analysisMenuBar")
  public FreeMarkerView elementAnalysisMenuBar (FreeMarkerViewBuilder builder) {
    return builder.setUrl ("/edu/dfci/cccb/mev/web/views/elements/analysisMenuBar.ftl").build ();
  }
  
  @Bean (name = "elements/sideNavigationBar")
  public FreeMarkerView elementSideNavigationBar (FreeMarkerViewBuilder builder) {
    return builder.setUrl ("/edu/dfci/cccb/mev/web/views/elements/sideNavigationBar.ftl").build ();
  }

  @Bean (name = "elements/expressionPanel")
  public FreeMarkerView elementExpressionPanel (FreeMarkerViewBuilder builder) {
    return builder.setUrl ("/edu/dfci/cccb/mev/web/views/elements/expressionPanel.ftl").build ();
  }

  @Bean (name = "elements/hierarchicalbody")
  public FreeMarkerView elementHierarchicalbody (FreeMarkerViewBuilder builder) {
    return builder.setUrl ("/edu/dfci/cccb/mev/web/views/elements/hierarchicalbody.ftl").build ();
  }

  @Bean (name = "elements/kMeansBody")
  public FreeMarkerView elementKMeansBody (FreeMarkerViewBuilder builder) {
    return builder.setUrl ("/edu/dfci/cccb/mev/web/views/elements/kMeansBody.ftl").build ();
  }

  @Bean (name = "elements/limmaBody")
  public FreeMarkerView elementLimmaBody (FreeMarkerViewBuilder builder) {
    return builder.setUrl ("/edu/dfci/cccb/mev/web/views/elements/limmaBody.ftl").build ();
  }

  @Bean (name = "elements/modal")
  public FreeMarkerView elementModal (FreeMarkerViewBuilder builder) {
    return builder.setUrl ("/edu/dfci/cccb/mev/web/views/elements/modal.ftl").build ();
  }

  @Bean (name = "elements/limmaAccordionList")
  public FreeMarkerView elementLimmaAccordionList (FreeMarkerViewBuilder builder) {
    return builder.setUrl ("/edu/dfci/cccb/mev/web/views/elements/limmaAccordionList.ftl").build ();
  }
  
  @Bean (name = "elements/limmaAccordion")
  public FreeMarkerView elementLimmaAccordion (FreeMarkerViewBuilder builder) {
    return builder.setUrl ("/edu/dfci/cccb/mev/web/views/elements/limmaAccordion.ftl").build ();
  }
  
  @Bean (name = "elements/tTestAccordionList")
  public FreeMarkerView elementTTestAccordionList (FreeMarkerViewBuilder builder) {
    return builder.setUrl ("/edu/dfci/cccb/mev/web/views/elements/tTestAccordionList.ftl").build ();
  }
  
  @Bean (name = "elements/tTestAccordion")
  public FreeMarkerView elementTTestAccordion (FreeMarkerViewBuilder builder) {
    return builder.setUrl ("/edu/dfci/cccb/mev/web/views/elements/tTestAccordion.ftl").build ();
  }
  
  @Bean (name = "elements/anovaAccordionList")
  public FreeMarkerView elementAnovaAccordionList (FreeMarkerViewBuilder builder) {
    return builder.setUrl ("/edu/dfci/cccb/mev/web/views/elements/anovaAccordionList.ftl").build ();
  }
  
  @Bean (name = "elements/anovaAccordion")
  public FreeMarkerView elementAnovaAccordion (FreeMarkerViewBuilder builder) {
    return builder.setUrl ("/edu/dfci/cccb/mev/web/views/elements/anovaAccordion.ftl").build ();
  }
  
  @Bean (name = "elements/hclAccordionList")
  public FreeMarkerView elementHclAccordionList (FreeMarkerViewBuilder builder) {
    return builder.setUrl ("/edu/dfci/cccb/mev/web/views/elements/hclAccordionList.ftl").build ();
  }
  
  @Bean (name = "elements/kmeansAccordionList")
  public FreeMarkerView elementKmeansAccordionList (FreeMarkerViewBuilder builder) {
    return builder.setUrl ("/edu/dfci/cccb/mev/web/views/elements/kmeansAccordionList.ftl").build ();
  }
  
  @Bean (name = "elements/kmeansAccordion")
  public FreeMarkerView elementKmeansAccordion (FreeMarkerViewBuilder builder) {
    return builder.setUrl ("/edu/dfci/cccb/mev/web/views/elements/kmeansAccordion.ftl").build ();
  }

  @Bean (name = "elements/table")
  public FreeMarkerView elementTable (FreeMarkerViewBuilder builder) {
    return builder.setUrl ("/edu/dfci/cccb/mev/web/views/elements/table.ftl").build ();
  }

  @Bean (name = "elements/mainNavigation")
  public FreeMarkerView elementMainNavigation (FreeMarkerViewBuilder builder) {
    return builder.setUrl ("/edu/dfci/cccb/mev/web/views/elements/mainNavigation.ftl").build ();
  }

  @Bean (name = "elements/heatmapPanels")
  public FreeMarkerView elementHeatmapPanels (FreeMarkerViewBuilder builder) {
    return builder.setUrl ("/edu/dfci/cccb/mev/web/views/elements/heatmapPanels.ftl").build ();
  }

  @Bean (name = "elements/visHeatmap")
  public FreeMarkerView elementVisHeatmap (FreeMarkerViewBuilder builder) {
    return builder.setUrl ("/edu/dfci/cccb/mev/web/views/elements/visHeatmap.ftl").build ();
  }

  @Bean (name = "elements/uploadDragAndDrop")
  public FreeMarkerView elementUploadDragAndDrop (FreeMarkerViewBuilder builder) {
    return builder.setUrl ("/edu/dfci/cccb/mev/web/views/elements/uploadDragAndDrop.ftl").build ();
  }

  @Bean (name = "elements/datasetSummary")
  public FreeMarkerView elementDatasetSummary (FreeMarkerViewBuilder builder) {
    return builder.setUrl ("/edu/dfci/cccb/mev/web/views/elements/datasetSummary.ftl").build ();
  }

  @Bean (name = "elements/hierarchicalAccordion")
  public FreeMarkerView elementHierarchicalAccordion (FreeMarkerViewBuilder builder) {
    return builder.setUrl ("/edu/dfci/cccb/mev/web/views/elements/hierarchicalAccordion.ftl").build ();
  }

  @Bean (name = "elements/uploadsTable")
  public FreeMarkerView elementUploadsTable (FreeMarkerViewBuilder builder) {
    return builder.setUrl ("/edu/dfci/cccb/mev/web/views/elements/uploadsTable.ftl").build ();
  }

  //SetManager templates  
  @Bean (name="elements/setmanager/selectionSetManager")
  public FreeMarkerView selectionSetManager(FreeMarkerViewBuilder builder){    
    return builder.setUrl ("/edu/dfci/cccb/mev/web/views/elements/setmanager/selectionSetManager.ftl").build ();
  }
  @Bean (name="elements/setmanager/selectionSetList")
  public FreeMarkerView selection(FreeMarkerViewBuilder builder){    
    return builder.setUrl ("/edu/dfci/cccb/mev/web/views/elements/setmanager/selectionSetList.ftl").build ();
  }
  @Bean (name="elements/setmanager/selectionSetEditForm")
  public FreeMarkerView selectionSetEditForm(FreeMarkerViewBuilder builder){    
    return builder.setUrl ("/edu/dfci/cccb/mev/web/views/elements/setmanager/selectionSetEditForm.ftl").build ();
  }
  @Bean (name="elements/mainpanel/MainPanel")
  public FreeMarkerView mainPanel(FreeMarkerViewBuilder builder){    
    return builder.setUrl ("/edu/dfci/cccb/mev/web/views/elements/mainpanel/MainPanel.ftl").build ();
  }

  //Presets templates
  @Bean(name="elements/presets/presetList")
  public FreeMarkerView presetList(FreeMarkerViewBuilder builder){
    return builder.setUrl ("/edu/dfci/cccb/mev/web/views/elements/presets/presetList.ftl").build ();
  }
  
  
  // Partials

  @Bean (name = "partials/partial1")
  public FreeMarkerView partial1 (FreeMarkerViewBuilder builder) {
    return builder.setUrl ("/edu/dfci/cccb/mev/web/views/partials/partial1.ftl").build ();
  }

  @Bean (name = "partials/heatmap")
  public FreeMarkerView heatmap (FreeMarkerViewBuilder builder) {
    return builder.setUrl ("/edu/dfci/cccb/mev/web/views/partials/heatmap.ftl").build ();
  }

  @Bean (name = "partials/importItems")
  public FreeMarkerView importitems (FreeMarkerViewBuilder builder) {
    return builder.setUrl ("/edu/dfci/cccb/mev/web/views/partials/importItems.ftl").build ();
  }
  
  @Bean (name = "partials/home")
  public FreeMarkerView homePage (FreeMarkerViewBuilder builder) {
    return builder.setUrl ("/edu/dfci/cccb/mev/web/views/partials/home.ftl").build ();
  }
    
}

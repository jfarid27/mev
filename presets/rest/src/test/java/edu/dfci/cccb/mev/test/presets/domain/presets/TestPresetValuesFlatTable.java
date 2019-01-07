package edu.dfci.cccb.mev.test.presets.domain.presets;

import static org.hamcrest.core.Is.is;
import static org.jooq.impl.DSL.using;
import static org.junit.Assert.assertThat;

import java.sql.SQLException;

import javax.inject.Inject;
import javax.inject.Named;
import javax.sql.DataSource;

import org.jooq.DSLContext;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import edu.dfci.cccb.mev.presets.contract.PresetDatasetBuilder;
import edu.dfci.cccb.mev.presets.dataset.flat.PresetValuesFlatTable;
import edu.dfci.cccb.mev.presets.dataset.flat.PresetValuesLogScaleAdapter;
import edu.dfci.cccb.mev.test.presets.rest.configuration.PresetsRestConfigurationTest;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(classes={PresetsRestConfigurationTest.class})
public class TestPresetValuesFlatTable {

  @Inject @Named("presets-datasource") DataSource dataSource;
  @Inject PresetDatasetBuilder presetDatasetBuilder;
  DSLContext context;
  @Before
  public void init() throws SQLException{
    context=using(dataSource.getConnection ());
  }
  
  @Test
  public void testGet () throws Exception {
    try(PresetValuesLogScaleAdapter values = new PresetValuesLogScaleAdapter (new PresetValuesFlatTable (context, "TEST_PRESET_VALUES_FLAT_TABLE"))){     
      double g1s1 = values.get ("g1", "SAMPLE1");    
      assertThat(g1s1, is(Math.log(0.1)));
      double g4s2 = values.get ("g4", "sAmple2");
      assertThat(g4s2, is(Math.log(0.42)));
      
    }
  }  
}

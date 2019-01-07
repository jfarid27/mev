package edu.dfci.cccb.mev.web.test.t_test.rest.controllers;

import static edu.dfci.cccb.mev.dataset.domain.contract.Dimension.Type.COLUMN;
import static java.util.Arrays.asList;
import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertThat;

import java.util.Properties;

import javax.inject.Inject;

import edu.dfci.cccb.mev.annotation.server.configuration.AnnotationProjectManagerConfiguration;
import edu.dfci.cccb.mev.annotation.server.configuration.AnnotationServerConfiguration;
import edu.dfci.cccb.mev.presets.rest.configuration.PresetsRestConfiguration;
import edu.dfci.cccb.mev.test.annotation.server.configuration.ProbeAnnotationsPersistanceConfigTest;
import lombok.extern.log4j.Log4j;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpSession;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.web.WebAppConfiguration;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultHandlers;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import com.fasterxml.jackson.databind.ObjectMapper;

import edu.dfci.cccb.mev.dataset.domain.contract.Dataset;
import edu.dfci.cccb.mev.dataset.domain.contract.DatasetBuilder;
import edu.dfci.cccb.mev.dataset.domain.contract.DatasetBuilderException;
import edu.dfci.cccb.mev.dataset.domain.contract.Dimension.Type;
import edu.dfci.cccb.mev.dataset.domain.contract.Analysis;
import edu.dfci.cccb.mev.dataset.domain.contract.InvalidDatasetNameException;
import edu.dfci.cccb.mev.dataset.domain.contract.InvalidDimensionTypeException;
import edu.dfci.cccb.mev.dataset.domain.contract.Selection;
import edu.dfci.cccb.mev.dataset.domain.contract.Workspace;
import edu.dfci.cccb.mev.dataset.domain.mock.MapBackedValueStoreBuilder;
import edu.dfci.cccb.mev.dataset.domain.mock.MockTsvInput;
import edu.dfci.cccb.mev.dataset.domain.simple.SimpleDatasetBuilder;
import edu.dfci.cccb.mev.dataset.domain.simple.SimpleSelection;
import edu.dfci.cccb.mev.dataset.domain.supercsv.SuperCsvParserFactory;
import edu.dfci.cccb.mev.dataset.rest.configuration.DatasetRestConfiguration;
import edu.dfci.cccb.mev.t_test.domain.contract.TTest;
import edu.dfci.cccb.mev.t_test.rest.configuration.TTestRestConfiguration;
import edu.dfci.cccb.mev.t_test.rest.controllers.TTestAnalysisController.OneSampleTTestDTO;
import edu.dfci.cccb.mev.t_test.rest.controllers.TTestAnalysisController.PairedSampleTTestDTO;
import edu.dfci.cccb.mev.t_test.rest.controllers.TTestAnalysisController.TwoSampleTTestDTO;
import edu.dfci.cccb.mev.web.configuration.DispatcherConfiguration;
import edu.dfci.cccb.mev.web.configuration.PersistenceConfiguration;
import edu.dfci.cccb.mev.web.configuration.container.ContainerConfigurations;

@Log4j
@WebAppConfiguration
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(classes={DispatcherConfiguration.class, 
                               PersistenceConfiguration.class, 
                               ContainerConfigurations.class, 
                               DatasetRestConfiguration.class,
                               AnnotationProjectManagerConfiguration.class,
                               PresetsRestConfiguration.class,
                               ProbeAnnotationsPersistanceConfigTest.class,
                               TTestRestConfiguration.class})
public class TestTTestAnalysisController {

  @Inject WebApplicationContext applicationContext;
  private MockMvc mockMvc;
  private MockHttpSession mockHttpSession;
  @Inject ObjectMapper jsonObjectMapper; 
  private Workspace workspace;
  @Inject DatasetBuilder datasetBuilder;
  
  private Dataset dataset;
  @Before
  public void setup() throws DatasetBuilderException, InvalidDatasetNameException, InvalidDimensionTypeException{
    
    //init mvc context
    mockMvc = MockMvcBuilders.webAppContextSetup (applicationContext).build ();    
    //init session
    mockHttpSession = new MockHttpSession ();    
    //init request
    MockHttpServletRequest mockHttpRequest = new MockHttpServletRequest ();
    mockHttpRequest.setSession (mockHttpSession);
    //bind current thread context to the request object
    RequestContextHolder.setRequestAttributes(new ServletRequestAttributes(mockHttpRequest));
    
    //get workspace
    workspace = applicationContext.getBean (Workspace.class);
    //create dataset
    dataset = datasetBuilder.build (new MockTsvInput ("mock", "\tS1\tS2\tS3\tS4\tS5\tS6\tS7\tS8\tS9\tS10\nG1\t1.188514712463404699e+01\t1.204140233660780979e+01\t1.234081412888200902e+01\t1.163898490634033323e+01\t1.236031434743662594e+01\t1.263358678802757851e+01\t8.279956752711142842e-01\t1.370350032209602054e+00\t1.037994449169919786e+00\t1.799723880674055643e-01\nG2\t1.885238297671112173e+00\t1.336588864883923256e+00\t8.726933138377110932e-01\t1.000704965530543999e+00\t1.217150222216657252e+00\t1.665733953332364292e+00\t9.910461446315059764e-01\t8.893733573944051507e-01\t7.208001799521422948e-01\t1.426827801481250546e+00\nG3\t1.188388854775392689e+00\t2.248097448451613634e-02\t4.585539956464407263e-01\t6.321050648189079357e-01\t2.288129746374324203e+00\t1.450523320935948490e+00\t8.743715686250737307e-02\t1.158676271225091536e+00\t4.483649242829604864e-01\t9.220164357611759787e-01\nG4\t1.262581518849290063e+01\t1.097643408373854257e+01\t1.248733733542591118e+01\t1.183359244927881093e+01\t1.242754369105501233e+01\t1.154062810662884253e+01\t1.402161753382923148e+00\t1.110723816716849388e+00\t5.519559139121001934e-01\t1.191073330691207799e+00\nG5\t4.212630682277810901e-01\t1.237089222272245159e+00\t3.942134331403249758e-01\t6.571736269682573184e-01\t7.107561591312392402e-01\t-1.799850179649653548e-02\t5.900194955064834712e-01\t1.866021689210231704e+00\t1.727950989673915139e+00\t1.673238760870431552e+00\nG6\t1.623904902317880872e+00\t2.033737954042716289e+00\t1.059667307687251370e+00\t5.829633567125455107e-01\t7.066180780581627907e-01\t7.179278333133176648e-01\t-1.620837539166711527e-01\t1.103900303479948608e+00\t1.000963636766493581e+00\t1.224700740396261756e+00\nG7\t1.089347908225312800e+00\t5.027904142354412409e-01\t1.526308400742132365e+00\t7.910323975010494779e-01\t5.334721895871848973e-01\t-6.584953162406614879e-02\t1.487086356403074960e+00\t7.289110071229999432e-01\t9.902665697489260177e-01\t5.816465061333875308e-01\nG8\t8.485257423886340966e-01\t2.465461094159465461e+00\t1.349261064626203988e+00\t6.143236099481832468e-01\t1.185825425854624138e+00\t1.244199580018195528e+00\t1.853690475642675750e+00\t-3.717669448902238827e-02\t6.854264339399868122e-01\t1.383830132326594464e-01\nG9\t1.403874515733180672e+00\t2.140084051686685029e+00\t1.143839053529577043e+00\t1.207999104285488734e+00\t1.209300244849931039e+00\t2.379590313511170674e-02\t1.974749101374898563e+00\t1.070609451318704108e+00\t6.151091514422060008e-02\t9.736475782769393739e-01\nG10\t7.899488648703185678e-01\t1.999391228179024704e+00\t9.363406180457896077e-01\t8.661591803373676068e-01\t9.989733067095830643e-01\t6.946825858089700212e-01\t9.689452303609590311e-01\t8.093542982306978217e-02\t1.095035589870868353e+00\t9.723302437465745074e-01\nG11\t3.334731085908727577e-01\t3.426242846092957262e-01\t4.937020871446952397e-02\t4.302553294755867563e-01\t1.153920107411710028e+00\t1.070122611874620544e+00\t1.908926108120575504e+00\t1.952270474332367778e+00\t7.019370450079993884e-02\t4.295688624516629250e-01\nG12\t1.392406509736836329e+00\t1.372870779384700590e+00\t5.404078368283642408e-01\t9.577725746500534987e-01\t1.930301589849148236e+00\t1.065669702544272957e+00\t1.022885904312618166e+00\t6.427143424737080668e-01\t3.930179936499352267e-01\t9.990896938741014033e-01\nG13\t1.325633714376544425e+00\t7.040364121034963230e-01\t1.589435388962163076e+00\t2.284719993548981698e+00\t1.702734362224248121e+00\t1.412491307931115214e+00\t1.672459629461545694e+00\t1.073155180531838671e+00\t1.411681767952686073e+00\t6.238961028115959095e-01\nG14\t1.288950803566242254e+00\t1.028575691034351314e-01\t1.102664086811557587e+00\t4.699036879893047081e-01\t3.876866425390312898e-01\t1.416878751168134887e+00\t9.433454890035535279e-01\t1.413906277410074352e+00\t4.778216530655552963e-01\t4.268871754146941200e-02\nG15\t1.322327157267192854e+00\t1.030668314264236773e+00\t1.171973213151947357e+00\t7.051026378379774417e-01\t1.603389007795869015e-01\t1.013608197193421656e+00\t1.119755089175789919e+00\t8.846284618247358678e-01\t1.032390130479277301e+00\t1.563217432008172647e-01\nG16\t4.594649795375105317e-02\t1.391620831679649006e+00\t1.543638392839115081e+00\t1.250790629220879691e+00\t1.760626870246531972e+00\t-2.719273644194430961e-01\t1.228157198567842556e+00\t5.718455067192516061e-01\t1.223007328253810089e+00\t1.320469664101578511e+00\nG17\t1.277799857436148834e+01\t1.275249893990934069e+01\t1.233011570757031095e+01\t1.245797072549325613e+01\t1.144397636751770087e+01\t1.264147576903921966e+01\t9.861790550751070583e-01\t1.005304796569497894e+00\t9.890599521756782142e-01\t8.110532346285517225e-01\nG18\t1.581739895645190686e+00\t2.054658403772655628e-01\t1.277027695188764245e+00\t1.460942482509764773e+00\t1.475607347809501535e+00\t4.862547500778641174e-01\t3.462212302333083080e-01\t1.181704073194318472e+00\t1.447215677951656065e+00\t1.239332864373165588e+00\nG19\t8.294625023542446796e-01\t4.347079214591751395e-01\t1.285626053170358718e+00\t1.773088394837966764e+00\t1.184014487786790193e+00\t5.522823407713599586e-01\t5.705450257984490126e-01\t2.110152326668979228e+00\t1.325059483533218563e+00\t2.663088059378119210e-01\nG20\t7.120160033627074281e-01\t9.123263397371228489e-01\t1.431553131076708674e+00\t1.724438313837745040e+00\t1.327524752620593329e+00\t2.585233328583039780e-01\t9.156477637307659512e-01\t8.222344601683477805e-01\t7.215323977152153789e-01\t6.489097589989401005e-01"));    
    //save the dataset
    workspace.put (dataset);
  }
  
  @Test 
  //input: {"name":"paired-sample-test","experimentName":"control","pValue":0.05,"multTestCorrection":false,"userMean":0.52}
  //output: {"name":"paired-sample-test","timestamp":{"timeInMillis":1396646985032,"seconds":45,"minutes":29,"hours":5,"period":"PM"},"type":"t-Test Analysis","results":[{"id":"G1","pValue":5.64352655899961E-9},{"id":"G2","pValue":0.00102319347341973},{"id":"G3","pValue":0.0703175246574431},{"id":"G4","pValue":1.09550584010499E-7},{"id":"G5","pValue":0.120093364018105},{"id":"G6","pValue":0.0147204178348411},{"id":"G7","pValue":0.0832003043348911},{"id":"G8","pValue":0.0106814701887212},{"id":"G9","pValue":0.0195272359606195},{"id":"G10","pValue":0.00941616788627118},{"id":"G11","pValue":0.143066610845363},{"id":"G12","pValue":0.00411494473073828},{"id":"G13","pValue":0.00192150847587894},{"id":"G14","pValue":0.0568839677134631},{"id":"G15","pValue":0.0121491122782274},{"id":"G16","pValue":0.0975215931154356},{"id":"G17","pValue":2.51823462690474E-8},{"id":"G18","pValue":0.0174711428255005},{"id":"G19","pValue":0.013786441367879},{"id":"G20","pValue":0.0137764311190548}]}
  public void testStartOneSample () throws Exception {
    Selection selection = new SimpleSelection ("control", new Properties (), asList ("S1", "S2", "S3","S4", "S5", "S6"));    
    dataset.dimension (COLUMN).selections ().put (selection);
    String analysesName = "one-sample-test";
    OneSampleTTestDTO dto = new OneSampleTTestDTO(analysesName, selection.name (), 0.05, false, 0.52);
    String jsonDto = jsonObjectMapper.writeValueAsString (dto);
    log.debug ("jsonDto: " + jsonDto);
    
    @SuppressWarnings ("unused") 
    MvcResult result = mockMvc.perform (
                                        MockMvcRequestBuilders.post ("/dataset/"+dataset.name ()+"/analyze/one_sample_ttest")
                                        .param ("format", "json")
                                        .contentType (MediaType.APPLICATION_JSON)
                                        .content (jsonDto)
                                        .session (mockHttpSession)
                                        .accept (MediaType.APPLICATION_JSON))
    .andDo (MockMvcResultHandlers.print ())
    .andExpect (MockMvcResultMatchers.status ().isOk ())
    .andReturn ();
    
    //The first put will generate an AnalysisStatus object with "IN_PROGRESS" status
    Analysis analysisStatus = dataset.analyses ().get (dto.name());
    log.debug("******* AnalysisStatus:\n"+ jsonObjectMapper.writeValueAsString (analysisStatus));      
    assertThat(analysisStatus.name (), is(dto.name()));        
    assertThat(analysisStatus.type (), is("ttest"));
    assertThat(analysisStatus.status (), is(Analysis.MEV_ANALYSIS_STATUS_IN_PROGRESS));        
     
    //Wait for analysis to complete
    Thread.sleep (3000L);
    
    TTest analysis = (TTest)dataset.analyses ().get (analysesName);    
    String jsonTTest = jsonObjectMapper.writeValueAsString (analysis);
    log.debug ("jsonAnalysis: " + jsonTTest);
  }

  @Test 
  //input: {"name": "two-sample-test","experimentName": "experiment","controlName": "control","pValue": 0.05,"multTestCorrection": false,"assumeEqualVariance": false}
  //output: {"name":"two-sample-test","timestamp":{"timeInMillis":1396644435207,"seconds":15,"minutes":47,"hours":4,"period":"PM"},"type":"t-Test Analysis","results":[{"id":"G1","pValue":1.75605253449618E-7},{"id":"G2","pValue":0.179536967211154},{"id":"G3","pValue":0.413303424964143},{"id":"G4","pValue":7.4414407968959E-10},{"id":"G5","pValue":0.0459406821671775},{"id":"G6","pValue":0.442195333404115},{"id":"G7","pValue":0.487794989322302},{"id":"G8","pValue":0.264859768724727},{"id":"G9","pValue":0.738247666598551},{"id":"G10","pValue":0.4100565631557},{"id":"G11","pValue":0.373189671778418},{"id":"G12","pValue":0.106313946603425},{"id":"G13","pValue":0.35226478832985},{"id":"G14","pValue":0.844958079335918},{"id":"G15","pValue":0.724268919768882},{"id":"G16","pValue":0.742394744844445},{"id":"G17","pValue":9.77999636830873E-9},{"id":"G18","pValue":0.937609351431979},{"id":"G19","pValue":0.904994722568906},{"id":"G20","pValue":0.258882323811034}]}
  public void testStartTwoSample () throws Exception {
    String analysesName = "two-sample-test";
    Selection control = new SimpleSelection ("control", new Properties (), asList ("S1", "S2", "S3","S4", "S5", "S6"));
    Selection experiment = new SimpleSelection ("experiment", new Properties (), asList ("S7", "S8", "S9","S10"));
    dataset.dimension (Type.COLUMN).selections ().put(control);
    dataset.dimension (Type.COLUMN).selections ().put(experiment);
    TwoSampleTTestDTO dto = new TwoSampleTTestDTO(analysesName, experiment.name (), control.name (), 0.05, false, false);
    String jsonDto = jsonObjectMapper.writeValueAsString (dto);
    log.debug ("jsonDto: " + jsonDto);
    
    @SuppressWarnings ("unused") 
    MvcResult result = mockMvc.perform (
                                        MockMvcRequestBuilders.post ("/dataset/"+dataset.name ()+"/analyze/two_sample_ttest")
                                        .param ("format", "json")
                                        .contentType (MediaType.APPLICATION_JSON)
                                        .content (jsonDto)
                                        .session (mockHttpSession)
                                        .accept (MediaType.APPLICATION_JSON))
    .andDo (MockMvcResultHandlers.print ())
    .andExpect (MockMvcResultMatchers.status ().isOk ())
    .andReturn ();
       
    //The first put will generate an AnalysisStatus object with "IN_PROGRESS" status
    Analysis analysisStatus = dataset.analyses ().get (dto.name());
    log.debug("******* AnalysisStatus:\n"+ jsonObjectMapper.writeValueAsString (analysisStatus));      
    assertThat(analysisStatus.name (), is(dto.name()));        
    assertThat(analysisStatus.type (), is("ttest"));
    assertThat(analysisStatus.status (), is(Analysis.MEV_ANALYSIS_STATUS_IN_PROGRESS));        
     
    //Wait for analysis to complete
    Thread.sleep (3000L);
    
    TTest analysis = (TTest)dataset.analyses ().get (analysesName);    
    String jsonTTest = jsonObjectMapper.writeValueAsString (analysis);
    log.debug ("jsonAnalysis: " + jsonTTest);
    
  }

  @Test 
  public void testStartPaired () throws Exception {
    String analysesName = "paired-sample-test";
    Dataset dataset = new SimpleDatasetBuilder ().setParserFactories (asList (new SuperCsvParserFactory ()))
            .setValueStoreBuilder (new MapBackedValueStoreBuilder ())
            .build (new MockTsvInput ("mock", "\tS1\tS2\tS3\tS4\tS5\tS6\tS7\tS8\tS9\tS10\nG1\t1.395864250608167323e+00\t3.916231968392996921e-01\t6.802546076106781303e-01\t2.842920356539057058e-01\t1.167978791803114591e+00\t5.575493844636111307e-01\t6.182690292679594579e-01\t1.064234628122129322e+00\t1.625811787848802759e+00\t9.864857446509973649e-01\nG2\t1.150244510668744669e+00\t1.388079975162571866e+00\t1.341088373821840873e+00\t3.084720816624257900e+00\t2.581845080221241595e+00\t2.367430922421942974e+00\t1.032362182840793619e+00\t3.117787544453723747e+00\t4.617892882371386065e-01\t1.602827033075911567e+00\nG3\t4.391889940305381268e-01\t1.382298236091139110e+00\t5.729514752270051803e-01\t9.725989889844147696e-01\t1.248284009105965708e+00\t8.926578096766990544e-01\t1.319754154382377642e+00\t1.540298352646559454e+00\t1.133127415001959681e+00\t1.676162412992144635e+00\nG4\t1.905561360016599703e+00\t9.356241886510197059e-02\t2.380285018347454984e-01\t3.847407463184190135e+00\t2.356291503584246882e+00\t2.794509930794697450e+00\t6.741638547114860991e-01\t1.724432926869718052e+00\t1.195998720063262244e+00\t1.745233671408940257e+00\nG5\t1.719337409578133302e+00\t3.494846145050719555e-01\t1.167667630790075517e+00\t4.782230260685107481e-01\t4.399142365221563322e-01\t6.800108190568048272e-01\t7.253248227503550360e-01\t7.806689816392595738e-01\t6.925188107811194316e-01\t3.442408758869031082e-02\nG6\t1.436621744373217435e+00\t9.752049715130082230e-01\t1.124556273672220330e+00\t7.312997978809270272e-01\t1.297314694459754891e+00\t2.485989754520180339e-01\t2.137179543584942110e+00\t4.094669660443884807e-01\t4.575113164318556636e-01\t8.482968148400671371e-01\nG7\t8.715438551197591810e-01\t6.180182417077844104e-01\t1.169918270963537843e+00\t2.862357749855996003e-01\t1.308055771196735995e+00\t3.779460416723794403e-01\t7.388802606792711503e-01\t9.796859959757528991e-01\t1.243557217256898939e+00\t8.095219518818874693e-02\nG8\t1.243952955865607057e+00\t1.440989147751386978e+00\t2.302367921964460218e-01\t6.786898563519169203e-01\t1.177747249932315610e+00\t2.131045195664008407e+00\t7.875240265935568029e-01\t7.395105287783704240e-01\t7.665894136772821721e-01\t1.303648953631650453e+00\nG9\t5.958003825885130889e-01\t1.709278882257981191e+00\t6.575371327833990920e-01\t3.329769660881695126e-01\t1.009206176972098135e+00\t2.588110927523207261e-01\t1.715273343737192757e+00\t7.197384843776438723e-01\t1.210154445852278160e+00\t5.181108688733959688e-01\nG10\t1.338637395387363371e+00\t8.298894986259539586e-01\t7.013115941652059426e-01\t8.935077793136788626e-01\t5.163333275847467041e-01\t1.610827343048899962e+00\t1.731075165538171534e+00\t2.237277410211655493e-01\t1.234319586039015482e+00\t2.412611683717998945e-01"));    
    Selection control = new SimpleSelection ("control", new Properties (), asList ("S1", "S2", "S3","S7", "S9"));
    Selection experiment = new SimpleSelection ("experiment", new Properties (), asList ("S4", "S5", "S6","S8", "S10"));
    dataset.dimension (COLUMN).selections ().put (control);
    dataset.dimension (COLUMN).selections ().put (experiment);
    workspace.put (dataset);
    
    PairedSampleTTestDTO dto = new PairedSampleTTestDTO(analysesName, experiment.name (), control.name (), 0.05, false);
    String jsonDto = jsonObjectMapper.writeValueAsString (dto);
    log.debug ("jsonDto: " + jsonDto);
    
    
    @SuppressWarnings ("unused") 
    MvcResult result = mockMvc.perform (
                                        MockMvcRequestBuilders.post ("/dataset/"+dataset.name ()+"/analyze/paired_ttest")    
                                        .param ("format", "json")
                                        .contentType (MediaType.APPLICATION_JSON)
                                        .content (jsonDto)
                                        .session (mockHttpSession)
                                        .accept (MediaType.APPLICATION_JSON))
    .andDo (MockMvcResultHandlers.print ())
    .andExpect (MockMvcResultMatchers.status ().isOk ())
    .andReturn ();
    
    //The first put will generate an AnalysisStatus object with "IN_PROGRESS" status
    Analysis analysisStatus = dataset.analyses ().get (dto.name());
    log.debug("******* AnalysisStatus:\n"+ jsonObjectMapper.writeValueAsString (analysisStatus));      
    assertThat(analysisStatus.name (), is(dto.name()));        
    assertThat(analysisStatus.type (), is("ttest"));
    assertThat(analysisStatus.status (), is(Analysis.MEV_ANALYSIS_STATUS_IN_PROGRESS));        
     
    //Wait for analysis to complete
    Thread.sleep (3000L);
    
    TTest analysis = (TTest)dataset.analyses ().get (analysesName);    
    String jsonTTest = jsonObjectMapper.writeValueAsString (analysis);
    log.debug ("jsonAnalysis: " + jsonTTest);
    
  }

}

package com.vzw.yang.test;

import static org.junit.Assert.*;

import java.io.File;
import java.io.IOException;

import org.junit.Test;

import com.fasterxml.jackson.core.JsonGenerationException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.vzw.yang.transformer.ucs.YangTransformer;

public class UCSMappersCienaUT {
 
    private static final ObjectMapper mapper = new ObjectMapper();

	@Test
	public void test1() {
        try {
          String file = "c:\\vendorC\\poc\\project\\python\\transformer\\CienaUTArray.json";
          JsonNode root = mapper.readTree(new File(file));
          System.out.println(root.toString());
          YangTransformer transformer = new YangTransformer();
          try {
				String json = transformer.jsonToYangJson("UT_ALARMS_CIENA_OBJ", (ObjectNode) root, false)   ;
				System.out.println(json);
			} catch (Exception e) {
				e.printStackTrace();
			}     
       } catch (JsonGenerationException e) {
          e.printStackTrace();
          fail("JsonGenerationException");
      } catch (JsonMappingException e) {
          e.printStackTrace();
          fail("JsonMappingException");
      } catch (IOException e) {
          e.printStackTrace();
          fail("IOException");
      }
  }
	
	@Test
	public void test2() {
        try {
            String file = "c:\\vendorC\\poc\\project\\python\\transformer\\CienaUTMeta.json";
            JsonNode root = mapper.readTree(new File(file));
            System.out.println(root.toString());
            YangTransformer transformer = new YangTransformer();
            try {
  				String json = transformer.jsonToYangJson("UT_ALARMS_CIENA_OBJ", (ObjectNode) root, false)   ;
  				System.out.println(json);
  			} catch (Exception e) {
  				e.printStackTrace();
  			}     
         } catch (JsonGenerationException e) {
            e.printStackTrace();
            fail("JsonGenerationException");
        } catch (JsonMappingException e) {
            e.printStackTrace();
            fail("JsonMappingException");
        } catch (IOException e) {
            e.printStackTrace();
            fail("IOException");
        }
	}
	
	@Test
	public void test3() {
        try {
            String file = "c:\\vendorC\\poc\\project\\python\\transformer\\CienaUTMetaAddAttrMissVal.json";
            JsonNode root = mapper.readTree(new File(file));
            System.out.println(root.toString());
            YangTransformer transformer = new YangTransformer();
            try {
  				String json = transformer.jsonToYangJson("UT_ALARMS_CIENA_OBJ", (ObjectNode) root, false)   ;
  				System.out.println(json);
  			} catch (Exception e) {
  				e.printStackTrace();
  			}     
         } catch (JsonGenerationException e) {
            e.printStackTrace();
            fail("JsonGenerationException");
        } catch (JsonMappingException e) {
            e.printStackTrace();
            fail("JsonMappingException");
        } catch (IOException e) {
            e.printStackTrace();
            fail("IOException");
        }
	}
	@Test
	public void test4() {
        try {
            String file = "c:\\vendorC\\poc\\project\\python\\transformer\\CienaUTMetaNoAddAttr.json";
            JsonNode root = mapper.readTree(new File(file));
            System.out.println(root.toString());
            YangTransformer transformer = new YangTransformer();
            try {
  				String json = transformer.jsonToYangJson("UT_ALARMS_CIENA_OBJ", (ObjectNode) root, false)   ;
  				System.out.println(json);
  			} catch (Exception e) {
  				e.printStackTrace();
  			}     
         } catch (JsonGenerationException e) {
            e.printStackTrace();
            fail("JsonGenerationException");
        } catch (JsonMappingException e) {
            e.printStackTrace();
            fail("JsonMappingException");
        } catch (IOException e) {
            e.printStackTrace();
            fail("IOException");
        }
	}
	@Test
	public void test5() {
        try {
            String file = "c:\\vendorC\\poc\\project\\python\\transformer\\CienaUTMetaNoAddAttr1.json";
            JsonNode root = mapper.readTree(new File(file));
            System.out.println(root.toString());
            YangTransformer transformer = new YangTransformer();
            try {
  				String json = transformer.jsonToYangJson("UT_ALARMS_CIENA_OBJ", (ObjectNode) root, false)   ;
  				System.out.println(json);
  			} catch (Exception e) {
  				e.printStackTrace();
  			}     
         } catch (JsonGenerationException e) {
            e.printStackTrace();
            fail("JsonGenerationException");
        } catch (JsonMappingException e) {
            e.printStackTrace();
            fail("JsonMappingException");
        } catch (IOException e) {
            e.printStackTrace();
            fail("IOException");
        }
	}
	@Test
	public void test6() {
        try {
            String file = "c:\\vendorC\\poc\\project\\python\\transformer\\CienaUTMetaNoAddAttr2.json";
            JsonNode root = mapper.readTree(new File(file));
            System.out.println(root.toString());
            YangTransformer transformer = new YangTransformer();
            try {
  				String json = transformer.jsonToYangJson("UT_ALARMS_CIENA_OBJ", (ObjectNode) root, false)   ;
  				System.out.println(json);
  			} catch (Exception e) {
  				e.printStackTrace();
  			}     
         } catch (JsonGenerationException e) {
            e.printStackTrace();
            fail("JsonGenerationException");
        } catch (JsonMappingException e) {
            e.printStackTrace();
            fail("JsonMappingException");
        } catch (IOException e) {
            e.printStackTrace();
            fail("IOException");
        }
	}
	@Test
	public void test7() {
        try {
            String file = "c:\\vendorC\\poc\\project\\python\\transformer\\CienaUTMetaNoAddAttr3.json";
            JsonNode root = mapper.readTree(new File(file));
            System.out.println(root.toString());
            YangTransformer transformer = new YangTransformer();
            try {
  				String json = transformer.jsonToYangJson("UT_ALARMS_CIENA_OBJ", (ObjectNode) root, false)   ;
  				System.out.println(json);
  			} catch (Exception e) {
  				e.printStackTrace();
  			}     
         } catch (JsonGenerationException e) {
            e.printStackTrace();
            fail("JsonGenerationException");
        } catch (JsonMappingException e) {
            e.printStackTrace();
            fail("JsonMappingException");
        } catch (IOException e) {
            e.printStackTrace();
            fail("IOException");
        }
	}
	@Test
	public void test8() {
        try {
            String file = "c:\\vendorC\\poc\\project\\python\\transformer\\CienaUTMetaNoAddAttr4.json";
            JsonNode root = mapper.readTree(new File(file));
            System.out.println(root.toString());
            YangTransformer transformer = new YangTransformer();
            try {
  				String json = transformer.jsonToYangJson("UT_ALARMS_CIENA_OBJ", (ObjectNode) root, false)   ;
  				System.out.println(json);
  			} catch (Exception e) {
  				e.printStackTrace();
  			}     
         } catch (JsonGenerationException e) {
            e.printStackTrace();
            fail("JsonGenerationException");
        } catch (JsonMappingException e) {
            e.printStackTrace();
            fail("JsonMappingException");
        } catch (IOException e) {
            e.printStackTrace();
            fail("IOException");
        }
	}
	@Test
	public void test9() {
        try {
            String file = "c:\\vendorC\\poc\\project\\python\\transformer\\CienaUTObject.json";
            JsonNode root = mapper.readTree(new File(file));
            System.out.println(root.toString());
            YangTransformer transformer = new YangTransformer();
            try {
  				String json = transformer.jsonToYangJson("UT_ALARMS_CIENA_OBJ", (ObjectNode) root, false)   ;
  				System.out.println(json);
  			} catch (Exception e) {
  				e.printStackTrace();
  			}     
         } catch (JsonGenerationException e) {
            e.printStackTrace();
            fail("JsonGenerationException");
        } catch (JsonMappingException e) {
            e.printStackTrace();
            fail("JsonMappingException");
        } catch (IOException e) {
            e.printStackTrace();
            fail("IOException");
        }
	}
}



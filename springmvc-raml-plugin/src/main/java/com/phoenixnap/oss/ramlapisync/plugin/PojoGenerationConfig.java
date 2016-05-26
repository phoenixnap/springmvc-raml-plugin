package com.phoenixnap.oss.ramlapisync.plugin;

import org.apache.maven.plugins.annotations.Parameter;
import org.jsonschema2pojo.DefaultGenerationConfig;

public class PojoGenerationConfig extends DefaultGenerationConfig
{
   /**
    * IF this is set to true, we will pass on this configuration to the jsonschema2pojo library for creation of BigDecimals instead of Doubles
    */
   @Parameter(required = false, readonly = true, defaultValue = "false")
   protected Boolean schemaUseBigDecimals;

   @Override
   public boolean isUseBigDecimals()
   {
      if (schemaUseBigDecimals != null){
         return schemaUseBigDecimals;
      }
      return super.isUseBigDecimals();
   }
}

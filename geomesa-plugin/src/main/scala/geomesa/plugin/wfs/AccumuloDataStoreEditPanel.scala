/*
 * Copyright 2013 Commonwealth Computer Research, Inc.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */


package geomesa.plugin.wfs

import org.apache.wicket.behavior.SimpleAttributeModifier
import org.apache.wicket.markup.html.form.validation.IFormValidator
import org.apache.wicket.markup.html.form.{FormComponent, Form}
import org.apache.wicket.model.{ResourceModel, IModel, PropertyModel}
import org.geoserver.catalog.DataStoreInfo
import org.geoserver.web.data.store.StoreEditPanel
import org.geoserver.web.data.store.panel.TextParamPanel
import org.geoserver.web.util.MapModel
import org.geotools.data.DataAccessFactory.Param

class AccumuloDataStoreEditPanel (componentId: String, storeEditForm: Form[_])
    extends StoreEditPanel(componentId, storeEditForm) {

  val model = storeEditForm.getModel
  setDefaultModel(model)
  val storeInfo = storeEditForm.getModelObject.asInstanceOf[DataStoreInfo]
  val paramsModel = new PropertyModel(model, "connectionParameters")
  val instanceId = addTextPanel(paramsModel, new Param("instanceId", classOf[String], "The Accumulo Instance ID", true))
  val zookeepers = addTextPanel(paramsModel, new Param("zookeepers", classOf[String], "Zookeepers", true))
  val user = addTextPanel(paramsModel, new Param("user", classOf[String], "User", true))
  val password = addTextPanel(paramsModel, new Param("password", classOf[String], "Password", true))
  val auths = addTextPanel(paramsModel, new Param("auths", classOf[String], "Authorizations", true))
  val tableName = addTextPanel(paramsModel, new Param("tableName", classOf[String], "The Accumulo Table Name", true))

  val dependentFormComponents = Array[FormComponent[_]](instanceId, zookeepers, user, password, tableName)
  dependentFormComponents.map(_.setOutputMarkupId(true))

  storeEditForm.add(new IFormValidator() {
    def getDependentFormComponents = dependentFormComponents

    def validate(form: Form[_]) {
      require(user.getValue != null)
      require(password.getValue != null)
      require(instanceId.getValue != null)
      require(tableName.getValue != null)
      require(zookeepers.getValue != null)
      require(auths.getValue != null)
    }
  })

  def addTextPanel(paramsModel: IModel[_], param: Param): FormComponent[_] = {
    val paramName = param.key
    val resourceKey = getClass.getSimpleName + "." + paramName
    val required = param.required
    val textParamPanel =
      new TextParamPanel(paramName,
        new MapModel(paramsModel, paramName).asInstanceOf[IModel[_]],
        new ResourceModel(resourceKey, paramName), required)
    textParamPanel.getFormComponent.setType(classOf[String])
    val defaultTitle = String.valueOf(param.description)
    val titleModel = new ResourceModel(resourceKey + ".title", defaultTitle)
    val title = String.valueOf(titleModel.getObject)
    textParamPanel.add(new SimpleAttributeModifier("title", title))
    add(textParamPanel)
    textParamPanel.getFormComponent
  }
}
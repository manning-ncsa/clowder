package controllers

import api.WithPermission
import api.Permission
<<<<<<< HEAD
import services.{AppConfiguration, AppConfigurationService, VersusPlugin}
import play.api.Play.current
import play.api.libs.concurrent.Execution.Implicits._

import play.api.Logger

import scala.concurrent._
import javax.inject.{Inject, Singleton}
=======

import services.{AppConfigurationService, AppAppearanceService, VersusPlugin}

import securesocial.core.providers.utils.RoutesHelper
import play.api.Play.current
import play.api.libs.concurrent.Execution.Implicits._

import play.api.libs.json.Json
import play.api.libs.json.Json._
import play.api.Logger

import scala.concurrent._
import play.api.libs.ws.WS
import play.api.libs.ws.Response
import play.api.libs.concurrent.Promise
>>>>>>> FETCH_HEAD

import play.api.data.Form
import play.api.data.Forms._
import javax.inject.{Inject, Singleton}

/**
 * Administration pages.
 *
 * @author Luigi Marini
 *
 */
@Singleton
class Admin extends SecuredController {

  def main = SecuredAction(authorization = WithPermission(Permission.Admin)) { request =>
    val theme = AppConfiguration.getTheme
    Logger.debug("Theme id " + theme)
    implicit val user = request.user
    Ok(views.html.admin(theme, AppConfiguration.getDisplayName, AppConfiguration.getWelcomeMessage))
  }
  
  def adminIndex = SecuredAction(authorization = WithPermission(Permission.Admin)) { request =>
    implicit val user = request.user
    Ok(views.html.adminIndex())
  }

  def reindexFiles = SecuredAction(parse.json, authorization = WithPermission(Permission.AddIndex)) { request =>
    Ok("Reindexing")
  }

  def test = SecuredAction(parse.json, authorization = WithPermission(Permission.Public)) { request =>
    Ok("""{"message":"test"}""").as(JSON)
  }

  def secureTest = SecuredAction(parse.json, authorization = WithPermission(Permission.Admin)) { request =>
    Ok("""{"message":"secure test"}""").as(JSON)
  }

  //get the available Adapters from Versus
  def getAdapters() = SecuredAction(authorization = WithPermission(Permission.Admin)) {
    request =>

      Async {
        current.plugin[VersusPlugin] match {

          case Some(plugin) => {

            var adapterListResponse = plugin.getAdapters()

            for {
              adapterList <- adapterListResponse
            } yield {
              Ok(adapterList.json)
            }

          } //case some

          case None => {
            Future(Ok("No Versus Service"))
          }
        } //match

      } //Async

  }

  // Get available extractors from Versus
  def getExtractors() = SecuredAction(authorization = WithPermission(Permission.Admin)) {
    request =>

      Async {
        current.plugin[VersusPlugin] match {

          case Some(plugin) => {

            var extractorListResponse = plugin.getExtractors()

            for {
              extractorList <- extractorListResponse
            } yield {
              Ok(extractorList.json)
            }
            //Ok(adapterListResponse)

          } //case some

          case None => {
            Future(Ok("No Versus Service"))
          }
        } //match

      } //Async

  }
  
  //Get available Measures from Versus 
  def getMeasures() = SecuredAction(authorization=WithPermission(Permission.Admin)){
     request =>
      
    Async{  
    	current.plugin[VersusPlugin] match {
     
        case Some(plugin)=>{
        	 
        	var measureListResponse= plugin.getMeasures()
        	 
        	for{
        	  measureList<-measureListResponse
        	}yield{
        	 Ok(measureList.json)
        	}
        	 //Ok(adapterListResponse)
        	         
            }//case some
         
		 case None=>{
		      Future(Ok("No Versus Service"))
		       }     
		 } //match
    
   } //Async
        
  }

  //Get available Indexers from Versus 
  def getIndexers() = SecuredAction(authorization = WithPermission(Permission.Admin)) {
    request =>

      Async {
        current.plugin[VersusPlugin] match {

          case Some(plugin) => {

            var indexerListResponse = plugin.getIndexers()

            for {
              indexerList <- indexerListResponse
            } yield {
              Ok(indexerList.json)
            }

          } //case some

          case None => {
            Future(Ok("No Versus Service"))
          }
        } //match

      } //Async

  }

  // Get adapter, extractor,measure and indexer value and send it to VersusPlugin to send a create index request to Versus 
  def createIndex() = SecuredAction(parse.json, authorization = WithPermission(Permission.Admin)) {
    implicit request =>
      Async {
        current.plugin[VersusPlugin] match {

          case Some(plugin) => {
            Logger.debug("INSIDE CreateIndex()")
            val adapter = (request.body \ "adapter").as[String]
            val extractor = (request.body \ "extractor").as[String]
            val measure = (request.body \ "measure").as[String]
            val indexer = (request.body \ "indexer").as[String]
            Logger.debug("Form Parameters: " + adapter + " " + extractor + " " + measure + " " + indexer);
            var reply = plugin.createIndex(adapter, extractor, measure, indexer)
            for (response <- reply) yield Ok(response.body)
          } //case some

          case None => {
            Future(Ok("No Versus Service"))
          }
        } //match

      } //Async
  }

  //Get list the of indexes from Versus 
  def getIndexes() = SecuredAction(authorization = WithPermission(Permission.Admin)) {
    request =>

      Async {
        current.plugin[VersusPlugin] match {

          case Some(plugin) => {
           Logger.debug("::::Inside getIndexes()::::")
            var indexListResponse = plugin.getIndexes()

            for {
              indexList <- indexListResponse
            } yield {
             if(indexList.body.isEmpty())
              { 
                Logger.debug(":::::::::::No:indexList.json")
                Ok("No Index")
                
              }
                else{
                  Ok(indexList.json)
                }
            }

          } //case some

          case None => {
            Future(Ok("No Versus Service"))
          }
        } //match

      } //Async
  }

  //build a specific index in Versus
  def buildIndex(id: String) = SecuredAction(authorization = WithPermission(Permission.Admin)) {
    request =>

      Async {
        current.plugin[VersusPlugin] match {

          case Some(plugin) => {

            var buildResponse = plugin.buildIndex(id)

            for {
              buildRes <- buildResponse
            } yield {
              Ok(buildRes.body)
            }

          } //case some

          case None => {
            Future(Ok("No Versus Service"))
          }
        } //match

      }

  }
  
  //Delete a specific index in Versus
  def deleteIndex(id: String)=SecuredAction(authorization=WithPermission(Permission.Admin)){
    request =>
    Async{  
      current.plugin[VersusPlugin] match {
     
        case Some(plugin)=>{
        	 
        	var deleteIndexResponse= plugin.deleteIndex(id)
        	 
        	for{
        	  deleteIndexRes<-deleteIndexResponse
        	}yield{
        	 Ok(deleteIndexRes.body)
        	}
        	 
        	         
            }//case some
         
		 case None=>{
		      Future(Ok("No Versus Service"))
		       }     
		 } //match
    
    }
  }

  //Delete all indexes in Versus

  def deleteAllIndexes() = SecuredAction(authorization = WithPermission(Permission.Admin)) {
    request =>

      Async {
        current.plugin[VersusPlugin] match {
        	
          case Some(plugin) => {

            var deleteAllResponse = plugin.deleteAllIndexes()

            for {
              deleteAllRes <- deleteAllResponse
            } yield {
              Ok(deleteAllRes.body)
            }

          } //case some

          case None => {
            Future(Ok("No Versus Service"))
          }
        } //match

      }
  }
  
  def setTheme() = SecuredAction(parse.json, authorization = WithPermission(Permission.Admin)) { implicit request =>
    request.body.\("theme").asOpt[String] match {
      case Some(theme) => {
        AppConfiguration.setTheme(theme)
        Ok("""{"status":"ok"}""").as(JSON)
      }
      case None => {
        Logger.error("no theme specified")
        BadRequest
      }
    }
  }

  val adminForm = Form(
  single(
    "email" -> email
  )verifying("Admin already exists.", fields => fields match {
     		case adminMail => !AppConfiguration.checkAdmin(adminMail)
     	})
)
  
  def newAdmin()  = SecuredAction(authorization=WithPermission(Permission.UserAdmin)) { implicit request =>
    implicit val user = request.user
  	Ok(views.html.newAdmin(adminForm))
  }
  
  def submitNew() = SecuredAction(authorization=WithPermission(Permission.UserAdmin)) { implicit request =>
    implicit val user = request.user
    user match {
      case Some(x) => {
        if (x.email.nonEmpty && AppConfiguration.checkAdmin(x.email.get)) {
          adminForm.bindFromRequest.fold(
            errors => BadRequest(views.html.newAdmin(errors)),
            newAdmin => {
              AppConfiguration.addAdmin(newAdmin)
              Redirect(routes.Admin.listAdmins())
            }
          )
        } else {
          Unauthorized("Not authorized")
        }
      }
    }
  }
  
  def listAdmins() = SecuredAction(authorization=WithPermission(Permission.UserAdmin)) { implicit request =>
    implicit val user = request.user
    user match {
      case Some(x) => {
        if (x.email.nonEmpty && AppConfiguration.checkAdmin(x.email.get)) {
          val admins = AppConfiguration.getAdmins
          Ok(views.html.listAdmins(admins))
        } else {
          Unauthorized("Not authorized")
        }
      }
    }
  }

}

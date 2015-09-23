//Method to remove the CO and redirect back to staging area on completion
function removeCuration(spaceId, curationId){
    console.log(spaceId);

    var request = jsRoutes.controllers.CurationObjects.deleteCuration(curationId).ajax({
        type: 'DELETE'
    });

    request.done(function (response, textStatus, jqXHR){
        window.location.href= "/spaces/"+ spaceId +"/stagingArea";
        console.log(response);
    });

    request.fail(function (jqXHR, textStatus, errorThrown){
        console.error("The following error occured: " + textStatus, errorThrown);
        var errMsg = "You must be logged in to delete a curation object from the system.";
        if (!checkErrorAndRedirect(jqXHR, errMsg)) {
            notify("The curation object was not deleted from the system due to : " + errorThrown, "error");
        }
    });
}


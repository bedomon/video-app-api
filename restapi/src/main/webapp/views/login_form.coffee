class Video.LoginForm extends Backbone.View
  template: "#login_form_tpl"
  className: "login-form"

  initialize: ->
    _.bindAll this, "render"
    _.extend(Video.LoginForm.prototype, Video.Utils)

  events:
    "click .js-login-submit" : "login"
    "click .js-login-cancel" : "cancel"
    "keypress .js-login-password" : "enter_password_login"


  render: ->
    container = $(@el)
    template = _.template( $(@template).html() )
    container.html(template)
    this
  
  enter_password_login: (e) ->
    console.log "enter_password_login"
    if e.charCode == 13
      @login(e)

  login: (e) ->
    self = @
    email = $(@el).find(".js-login-email").val()
    password = $(@el).find(".js-login-password").val()
    if email != "" && password != ""
      if self.validate_email(email)
        user = new Video.UserModel()
        user.url = window.Video.root_path + "users/login"
        user.fetch
          data:
            email: email
            password: password
          type: 'POST'
          contentType: "application/x-www-form-urlencoded"
          success: ->
            if user.get(0) && user.get(1)
              localStorage.token = user.get(1).token
              $(".popup").html ""
              self.render_main_menu()
            else
              $(self.el).find(".js-status").html "Invalid email or password."
              self.show_errors()
      else
        $(self.el).find(".js-login-email").addClass "error"
        $(self.el).find(".js-status").html "Invalid email address."
    else
      self.show_errors()
      $(self.el).find(".js-status").html "Fill all required fields."

  cancel: (e) ->
    $(".popup").html ""

  show_errors: ->
    $(@el).find(".js-login-email").addClass "error"
    $(@el).find(".js-login-password").addClass "error"

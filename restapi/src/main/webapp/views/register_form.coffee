class Video.RegisterForm extends Backbone.View
  template: "#register_form_tpl"
  className: "register-form"

  initialize: ->
    _.bindAll this, "render"
    _.extend(Video.RegisterForm.prototype, Video.Utils)

  events:
    "click .js-register-submit" : "register"
    "click .js-register-cancel" : "cancel"

  render: ->
    container = $(@el)
    template = _.template( $(@template).html() )
    container.html(template)
    this

  register: (e) ->
    self = @
    email = $(@el).find(".js-register-email").val()
    password = $(@el).find(".js-register-password").val()
    confirm_password = $(@el).find(".js-register-confirm-password").val()
    if email != "" && password != "" && confirm_password != ""
      if password == confirm_password
        if self.validate_email(email)
          user = new Video.UserModel()
          user.url = window.Video.root_path + "users/register"
          user.fetch
            data: { email: email, password: password }
            async:false
            type: 'POST'
            contentType: "application/x-www-form-urlencoded"
            success: ->
              if user.get(0)
                @login_form = new Video.LoginForm()
                $(".popup").html @login_form.render().el
        else
          $(@el).find(".js-register-email").addClass "error"
          $(self.el).find(".js-status").html "Invalid email address."
      else
        self.show_errors()
        $(self.el).find(".js-status").html "Passwords do not match"
    else
      $(self.el).find(".js-status").html "Fill all required fields."
      self.show_errors()

  cancel: (e) ->
    $(".popup").html ""

  show_errors: ->
    $(@el).find(".js-register-email").addClass "error"
    $(@el).find(".js-register-password").addClass "error"
    $(@el).find(".js-register-confirm-password").addClass "error"

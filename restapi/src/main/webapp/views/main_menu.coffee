class Video.MainMenu extends Backbone.View
  template: "#main_menu_tpl"
  className: "main-menu"

  initialize: ->
    _.bindAll this, "render"
    _.extend(Video.MainMenu.prototype, Video.Utils)

  events:
    "click .js-login" : "login"
    "click .js-logout" : "logout"
    "click .js-register" : "register"
    "click .js-upload" : "upload"

  render: ->
    container = $(@el)
    template = _.template( $(@template).html() )
    container.html(template)
    this

  login: (e) ->
    @login_form = new Video.LoginForm()
    $(".popup").html @login_form.render().el
    @render_main_menu()

  logout: (e) ->
    localStorage.removeItem("token")
    @render_main_menu()

  register: (e) ->
    @register_form = new Video.RegisterForm()
    $(".popup").html @register_form.render().el

  upload: (e) ->
    @upload_form = new Video.UploadForm()
    $(".popup").html @upload_form.render().el


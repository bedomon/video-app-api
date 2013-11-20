class Video.UserModel extends Backbone.Model
  urlRoot: window.Video.root_path + "users/"
  initialize: (options) ->
    @options = options

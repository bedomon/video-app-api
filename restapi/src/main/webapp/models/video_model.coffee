class Video.VideoModel extends Backbone.Model
  urlRoot: window.Video.root_path + "videos/"
  initialize: (options) ->
    @options = options

class Video.Videos extends Backbone.Collection
  model: Video.VideoModel
  url: window.Video.root_path + "videos/index"

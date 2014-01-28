class Video.Users extends Backbone.Collection
  model: Video.UserModel
  url: window.Video.root_path + "users"

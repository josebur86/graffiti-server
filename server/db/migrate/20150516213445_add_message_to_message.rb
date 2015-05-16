class AddMessageToMessage < ActiveRecord::Migration
  def change
    add_column :messages, :message, :string
  end
end

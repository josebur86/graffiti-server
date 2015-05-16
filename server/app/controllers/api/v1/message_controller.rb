module Api
    module V1

        class MessageController < ApplicationController
            def show
                @message = Message.last
                render json: {message: @message.message}, status: 200
            end

            def create
                @message = Message.new
                @message.message = params[:message]
                if @message.save
                    render json: {message: params[:message]}, status: 201
                else
                    render json: {error: "Could not save."}, status: 400
                end
            end
        end

    end
end

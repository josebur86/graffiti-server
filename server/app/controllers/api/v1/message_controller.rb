module Api
    module V1

        class MessageController < ApplicationController
            def show
                render json: {message: 'down for whatever?'}, status: 200
            end
        end

    end
end

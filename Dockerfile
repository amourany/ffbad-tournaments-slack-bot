FROM public.ecr.aws/aleph0io/lambda/java:17
COPY target/*-aws.jar "${LAMBDA_TASK_ROOT}/lib/"
CMD [ "org.springframework.cloud.function.adapter.aws.FunctionInvoker::handleRequest" ]
